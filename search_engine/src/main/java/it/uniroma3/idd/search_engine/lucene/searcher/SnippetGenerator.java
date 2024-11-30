package it.uniroma3.idd.search_engine.lucene.searcher;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;




@Component
public class SnippetGenerator {

    private static final StandardAnalyzer STANDARD_ANALYZER = new StandardAnalyzer();

    public void addSnippets(Set<Document> documents, Query query) {
        Highlighter highlighter = new Highlighter(new QueryScorer(query));
        highlighter.setTextFragmenter(new SimpleFragmenter(150));

        for (Document doc : documents) {
            String bestSnippet = null;
            for (String field : new String[]{"title", "authors", "paragraphs", "articleAbstract"}) {
                String fieldText = doc.get(field);
                if (fieldText != null) {
                    try {
                        String snippet = highlighter.getBestFragment(STANDARD_ANALYZER, field, fieldText);
                        if (snippet != null) {
                            bestSnippet = snippet;
                            break;
                        }
                    } catch (IOException | InvalidTokenOffsetsException ignored) {
                    }
                }
            }
            if (bestSnippet != null) {
                doc.add(new StoredField("snippet", bestSnippet));
            }
        }
    }
}
