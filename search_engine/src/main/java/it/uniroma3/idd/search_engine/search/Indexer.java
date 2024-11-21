package it.uniroma3.idd.search_engine.search;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.codecs.Codec;
import org.apache.lucene.codecs.simpletext.SimpleTextCodec;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.LMJelinekMercerSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.tests.analysis.TokenStreamToDot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class Indexer {

    private void indexDocs(Directory directory, Codec codec) throws IOException {
        Analyzer defaultAnalyzer = new StandardAnalyzer();
        CharArraySet stopWords = new CharArraySet(Arrays.asList("in", "dei", "di"), true);
        Map<String, Analyzer> perFieldAnalyzers = new HashMap<>();
        perFieldAnalyzers.put("contenuto", new StandardAnalyzer(stopWords));
        perFieldAnalyzers.put("titolo", new WhitespaceAnalyzer());

        Analyzer analyzer = new PerFieldAnalyzerWrapper(defaultAnalyzer, perFieldAnalyzers);
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        if (codec != null) {
            config.setCodec(codec);
        }
        IndexWriter writer = new IndexWriter(directory, config);
        writer.deleteAll();

        Document doc1 = new Document();
        doc1.add(new TextField("titolo", "Come diventare un ingegnere dei dati, Data Engineer?", Field.Store.YES));
        doc1.add(new TextField("contenuto", "Sembra che oggigiorno tutti vogliano diventare un Data Scientist  ...", Field.Store.YES));
        doc1.add(new StringField("data", "12 ottobre 2016", Field.Store.YES));
        doc1.add(new KnnFloatVectorField("embedding", new float[]{0.4f, 0.5f, 0.6f}));

        Document doc2 = new Document();
        doc2.add(new TextField("titolo", "Curriculum Ingegneria dei Dati - Sezione di Informatica e Automazione", Field.Store.YES));
        doc2.add(new TextField("contenuto", "Curriculum. Ingegneria dei Dati. Laurea Magistrale in Ingegneria Informatica ...", Field.Store.YES));
        doc2.add(new KnnFloatVectorField("embedding", new float[]{0.1f, 0.2f, 0.3f}));

        writer.addDocument(doc1);
        writer.addDocument(doc2);

        writer.commit();
        writer.close();
    }

}