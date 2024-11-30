package it.uniroma3.idd.search_engine.lucene.searcher;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.FloatField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;


@Component
public class SearchManager {

    private IndexSearcher searcher;

    public void initializeSearcher(String indexDirectory) {
        try {
            Path indexPath = Paths.get(indexDirectory);
            Directory directory = FSDirectory.open(indexPath);
            IndexReader reader = DirectoryReader.open(directory);
            this.searcher = new IndexSearcher(reader);
        } catch (IOException e) {
            throw new RuntimeException("Error initializing the searcher", e);
        }
    }

    public TopDocs executeQuery(Query query, int limit) {
        try {
            return searcher.search(query, limit);
        } catch (IOException e) {
            throw new RuntimeException("Error executing query", e);
        }
    }

    public Set<Document> retrieveDocuments(TopDocs topDocs) {
        Set<Document> documents = new HashSet<>();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            try {
                Document doc = searcher.storedFields().document(scoreDoc.doc);
                doc.add(new FloatField("score", scoreDoc.score, StoredField.Store.YES));
                documents.add(doc);
            } catch (IOException e) {
                throw new RuntimeException("Error retrieving document", e);
            }
        }
        return documents;
    }
}
