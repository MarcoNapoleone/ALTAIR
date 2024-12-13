package it.uniroma3.idd.search_engine.lucene.searcher;

import it.uniroma3.idd.search_engine.lucene.LuceneConfig;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.FloatField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Component
public class SearchManager {

    private IndexSearcher searcher;

    @Autowired
    private LuceneConfig luceneConfig;

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

    public List<Document> retrieveDocuments(TopDocs topDocs, Query query, Float tresholdMultiplier) {
        List<Document> documents = new ArrayList<>();

        if (topDocs.scoreDocs.length == 0) {
            return documents;
        }

        float maxScore = topDocs.scoreDocs[0].score;

        if(tresholdMultiplier == null) {
            tresholdMultiplier = luceneConfig.getTreasholdMultiplier();
        }

        float threshold = maxScore * tresholdMultiplier;
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            try {
                Document doc = searcher.storedFields().document(scoreDoc.doc);
                doc.add(new FloatField("score", scoreDoc.score, StoredField.Store.YES));
                if (scoreDoc.score > threshold) {
                    if (luceneConfig.isQueryExplain()) {
                        Explanation explanation = searcher.explain(query, scoreDoc.doc);
                        System.out.println(explanation);
                    }
                    documents.add(doc);
                }
            } catch (IOException e) {
                throw new RuntimeException("Error retrieving document", e);
            }
        }

        return documents;
    }


}
