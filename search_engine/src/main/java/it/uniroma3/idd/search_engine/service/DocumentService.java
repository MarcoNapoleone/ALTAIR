package it.uniroma3.idd.search_engine.service;

import it.uniroma3.idd.search_engine.lucene.searcher.LuceneSearcher;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional
public class DocumentService {

    private final LuceneSearcher luceneSearcher;

    @Autowired
    public DocumentService(LuceneSearcher luceneSearcher) {
        this.luceneSearcher = luceneSearcher;
    }

    public Document getDocument(Long id) {
        //TODO LUCENE
        return null;
    }

    public List<Document> getDocumentsQuery(Map<String,String> filters) throws ParseException, InvalidTokenOffsetsException, IOException {
        return luceneSearcher.searchArticles(filters);
    }

    public Document getAllDocuments() {
        //TODO LUCENE
        return null;
    }

}
