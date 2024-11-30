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
import java.util.Map;
import java.util.Set;



@Component
public class LuceneSearcher implements ApplicationListener<IndexingCompleteEvent> {

    private final LuceneConfig luceneConfig;
    private final SearchManager searchManager;
    private final QueryBuilder queryBuilder;
    private final SnippetGenerator snippetGenerator;

    @Autowired
    public LuceneSearcher(LuceneConfig luceneConfig, SearchManager searchManager, QueryBuilder queryBuilder, SnippetGenerator snippetGenerator) {
        this.luceneConfig = luceneConfig;
        this.searchManager = searchManager;
        this.queryBuilder = queryBuilder;
        this.snippetGenerator = snippetGenerator;
    }

    @Override
    public void onApplicationEvent(@NotNull IndexingCompleteEvent event) {
        searchManager.initializeSearcher(luceneConfig.getIndexDirectory());
    }

    public Set<Document> searchArticles(Map<String, String> filters) throws ParseException, InvalidTokenOffsetsException, IOException {
        Query query = queryBuilder.buildArticleQuery(filters);
        TopDocs topDocs = searchManager.executeQuery(query, Integer.parseInt(filters.getOrDefault("limit", "10")));
        Set<Document> documents = searchManager.retrieveDocuments(topDocs);
        snippetGenerator.addSnippets(documents, query);
        return documents;
    }

    public Set<Document> searchTables(String queryText, Integer limit, Boolean useEmbedding) throws ParseException, IOException {
        Query query = queryBuilder.buildTableQuery(queryText, useEmbedding, limit != null ? limit : 10);
        TopDocs topDocs = searchManager.executeQuery(query, limit != null ? limit : 10);
        return searchManager.retrieveDocuments(topDocs);
    }
}
