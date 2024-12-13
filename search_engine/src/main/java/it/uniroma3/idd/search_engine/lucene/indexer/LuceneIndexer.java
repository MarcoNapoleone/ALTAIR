package it.uniroma3.idd.search_engine.lucene.indexer;

import it.uniroma3.idd.search_engine.lucene.LuceneConfig;
import it.uniroma3.idd.search_engine.model.Article;
import it.uniroma3.idd.search_engine.model.Table;
import it.uniroma3.idd.search_engine.utils.Parser;
import jakarta.annotation.PostConstruct;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.codecs.Codec;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class LuceneIndexer {

    @Autowired
    private LuceneConfig luceneConfig;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    private AllMiniLmL6V2EmbeddingModel allMiniLmEmbedder;

    private static final Analyzer STANDARD_ANALYZER = new StandardAnalyzer();
    private static final Analyzer WHITESPACE_ANALYZER = new WhitespaceAnalyzer();
    private static final Analyzer SIMPLE_ANALYZER = new SimpleAnalyzer();


    @PostConstruct
    public void init() {
        try {
            // Log to monitor the flow
            System.out.println("Index initialization in progress...");
            if (luceneConfig.isShouldInitializeIndex()) {
                this.allMiniLmEmbedder = new AllMiniLmL6V2EmbeddingModel();
                System.out.println("Deleting the index directory...");
                deleteNonEmptyDirectory(Paths.get(luceneConfig.getIndexDirectory())); // Delete the index directory
                indexArticles(luceneConfig.getIndexDirectory(), Codec.getDefault()); // Initialize the index
                indexTables(luceneConfig.getIndexDirectory(), Codec.getDefault());
            }
            System.out.println("Table Index initialized, publishing event.");
            eventPublisher.publishEvent(new IndexingCompleteEvent(this)); // Publish the event upon completion
            System.out.println("IndexingComplete event published.");
        } catch (Exception e) {
            throw new RuntimeException("Error initializing the index", e);
        }

    }

    public void indexArticles(String Pathdir, Codec codec) throws IOException {
        Path path = Paths.get(Pathdir);
        Directory dir = FSDirectory.open(path);

        // Configure analyzers for fields
        Map<String, Analyzer> perFieldAnalyzers = new HashMap<>();
        perFieldAnalyzers.put("title", SIMPLE_ANALYZER);
        perFieldAnalyzers.put("authors", SIMPLE_ANALYZER);
        perFieldAnalyzers.put("paragraphs", STANDARD_ANALYZER);
        perFieldAnalyzers.put("articleAbstract", STANDARD_ANALYZER);
        Analyzer perFieldAnalyzer = new PerFieldAnalyzerWrapper(luceneConfig.customAnalyzer(), perFieldAnalyzers);
        IndexWriterConfig config = new IndexWriterConfig(perFieldAnalyzer);

        // Set the codec
        config.setCodec(codec);

        IndexWriter writer = new IndexWriter(dir, config);

        List<Article> articles = Parser.articleParser();

        for (Article article : articles) {
            Document doc = new Document();
            doc.add(new StringField("id", article.getId(), TextField.Store.YES));
            doc.add(new TextField("title", article.getTitle(), TextField.Store.YES));
            doc.add(new TextField("authors", String.join(" ", article.getAuthors()), TextField.Store.YES));
            doc.add(new TextField("paragraphs", String.join(" ", article.getParagraphs()), TextField.Store.YES));
            doc.add(new TextField("articleAbstract", article.getArticleAbstract(), TextField.Store.YES));
            writer.addDocument(doc);
        }

        writer.commit();
        writer.close();
    }

    public void indexTables(String Pathdir, Codec codec) throws Exception {
        Path path = Paths.get(Pathdir);
        Directory dir = FSDirectory.open(path);

        Map<String, Analyzer> perFieldAnalyzers = new HashMap<>();
        perFieldAnalyzers.put("caption", SIMPLE_ANALYZER);
        perFieldAnalyzers.put("body", WHITESPACE_ANALYZER);
        perFieldAnalyzers.put("footnotes", STANDARD_ANALYZER);
        perFieldAnalyzers.put("references", STANDARD_ANALYZER);
        Analyzer perFieldAnalyzer = new PerFieldAnalyzerWrapper(luceneConfig.customAnalyzer(), perFieldAnalyzers);
        IndexWriterConfig config = new IndexWriterConfig(perFieldAnalyzer);

        config.setCodec(codec);

        IndexWriter writer = new IndexWriter(dir, config);

        List<Table> tables = Parser.tableParser();

        for (Table table : tables) {
            Document doc = new Document();
            doc.add(new StringField("id", table.getId(), TextField.Store.YES));
            doc.add(new TextField("caption", table.getCaption(), TextField.Store.YES));
            doc.add(new StoredField("html_table", table.getBody()));
            doc.add(new TextField("body", table.getBodyCleaned(), Field.Store.NO));
            doc.add(new TextField("footnotes", table.getFootnotesString(), TextField.Store.YES));
            doc.add(new TextField("references", table.getReferencesString(), TextField.Store.YES));
            doc.add(new StringField("fileName", table.getFileName(), TextField.Store.YES));

            String combinedText = table.getCaption() + " "
                    + String.join(" ", table.getFootnotesString()) + " "
                    + String.join(" ", table.getReferencesString());

            if (combinedText.trim().isEmpty()) {
                continue;
            }

            try {
                TextSegment textSegment = TextSegment.from(combinedText);
                Embedding embedding = allMiniLmEmbedder.embed(textSegment).content();
                doc.add(new KnnFloatVectorField("embedding", embedding.vector()));
            } catch (IllegalArgumentException e) {
                System.out.println("Error embedding table: " + table.getId());
                continue;
            }

            writer.addDocument(doc);
        }

        writer.commit();
        writer.close();
    }

    public void deleteNonEmptyDirectory(Path directory) throws IOException {
        // Verifica se la directory esiste
        if (Files.exists(directory) && Files.isDirectory(directory)) {
            // Rimuove ricorsivamente i file e le sottocartelle
            Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);  // Elimina il file
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);  // Elimina la directory dopo aver cancellato i suoi contenuti
                    return FileVisitResult.CONTINUE;
                }
            });
            System.out.println("Directory and its contents deleted.");
        } else {
            System.out.println("Directory does not exist or is not a directory.");
        }
    }

}