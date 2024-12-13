package it.uniroma3.idd.search_engine.lucene.searcher;

import it.uniroma3.idd.search_engine.lucene.LuceneConfig;
import it.uniroma3.idd.search_engine.lucene.indexer.IndexingCompleteEvent;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;



@Component
public class LuceneSearcher implements ApplicationListener<IndexingCompleteEvent> {

    @Autowired
    private LuceneConfig luceneConfig;

    @Autowired
    private SearchManager searchManager;

    @Autowired
    private QueryBuilder queryBuilder;

    @Autowired
    private SnippetGenerator snippetGenerator;


    @Override
    public void onApplicationEvent(@NotNull IndexingCompleteEvent event) {
        searchManager.initializeSearcher(luceneConfig.getIndexDirectory());
    }

    public List<Document> searchArticles(Map<String, String> filters, Float tresholdMultiplier) throws ParseException, InvalidTokenOffsetsException, IOException {
        Query query = queryBuilder.buildArticleQuery(filters);
        TopDocs topDocs = searchManager.executeQuery(query, Integer.parseInt(filters.getOrDefault("limit", "10")));
        List<Document> documents = searchManager.retrieveDocuments(topDocs, query, tresholdMultiplier);
        snippetGenerator.addSnippets(documents, query);
        return documents;
    }

    public Document searchArticleById(Long id) throws ParseException {
        Query query = queryBuilder.buildArticleQuery(Map.of("id", id.toString()));
        TopDocs topDocs = searchManager.executeQuery(query, 1);
        List<Document> documents = searchManager.retrieveDocuments(topDocs, query, null);
        return documents.isEmpty() ? null : documents.get(0);
    }

    public List<Document> searchTables(String queryText, Integer limit, Boolean useEmbedding, Float tresholdMultiplier) {
        Query query = queryBuilder.buildTableQuery(queryText, useEmbedding, limit != null ? limit : 10, new String[]{"caption", "body", "footnotes", "references"});
        TopDocs topDocs = searchManager.executeQuery(query, limit != null ? limit : 10);
        return searchManager.retrieveDocuments(topDocs, query, tresholdMultiplier);
    }

    public Document searchTableById(Long id) {
        Query query = queryBuilder.buildTableQuery(id.toString(), false, 1, new String[]{"id"});
        TopDocs topDocs = searchManager.executeQuery(query, 1);
        List<Document> documents = searchManager.retrieveDocuments(topDocs, query, null);
        return documents.isEmpty() ? null : documents.getFirst();
    }
}
