package it.uniroma3.idd.search_engine.utils.lucene;

import it.uniroma3.idd.search_engine.model.Article;
import it.uniroma3.idd.search_engine.utils.IndexingCompleteEvent;
import it.uniroma3.idd.search_engine.utils.Parser;
import it.uniroma3.idd.search_engine.utils.Stopwords;
import jakarta.annotation.PostConstruct;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.codecs.simpletext.SimpleTextCodec;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.codecs.Codec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class LuceneIndexer {

    private final LuceneConfig luceneConfig;
    private static final CharArraySet STOP_WORDS = new CharArraySet(Stopwords.getStopwords(), true);
    private static final Analyzer STANDARD_ANALYZER = new StandardAnalyzer(STOP_WORDS);
    private static final Analyzer WHITESPACE_ANALYZER = new WhitespaceAnalyzer();
    private static final Analyzer KEYWORD_ANALYZER = new KeywordAnalyzer();
    private static final Analyzer SIMPLE_ANALYZER = new SimpleAnalyzer();
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public LuceneIndexer(LuceneConfig luceneConfig, ApplicationEventPublisher eventPublisher) {
        this.luceneConfig = luceneConfig;
        this.eventPublisher = eventPublisher;
    }

    @PostConstruct
    public void init() {
        try {
            // Log to monitor the flow
            System.out.println("Index initialization in progress...");
            if (luceneConfig.isShouldInitializeIndex()){
                System.out.println("Deleting the index directory...");
                deleteNonEmptyDirectory(Paths.get(luceneConfig.getIndexDirectory())); // Delete the index directory
                indexDocs(luceneConfig.getIndexDirectory(), Codec.getDefault()); // Initialize the index
            }
            System.out.println("Index initialized, publishing event.");
            eventPublisher.publishEvent(new IndexingCompleteEvent(this)); // Publish the event upon completion
            System.out.println("IndexingComplete event published.");
        } catch (IOException e) {
            throw new RuntimeException("Error initializing the index", e);
        }
    }

    public void indexDocs(String Pathdir, Codec codec) throws IOException {
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