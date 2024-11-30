package it.uniroma3.idd.search_engine.service;

import it.uniroma3.idd.search_engine.lucene.searcher.LuceneSearcher;
import it.uniroma3.idd.search_engine.model.Table;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class TableService {

    private final LuceneSearcher luceneSearcher;

    @Autowired
    public TableService(LuceneSearcher luceneSearcher) {
        this.luceneSearcher = luceneSearcher;
    }
    public List<Document> getTablesQuery(String query, Boolean useNLP, Integer limit, Boolean useEmbedding, Float tresholdMultiplier) throws ParseException, InvalidTokenOffsetsException, IOException {
        if (useNLP){
            // @TODO NLP search
            return luceneSearcher.searchTables(query, limit, useEmbedding, tresholdMultiplier);
        } else {
            return luceneSearcher.searchTables(query, limit, useEmbedding, tresholdMultiplier);
        }
    }
}
