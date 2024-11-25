package it.uniroma3.idd.search_engine.lucene.searcher;

import it.uniroma3.idd.search_engine.lucene.AcronymManager;
import it.uniroma3.idd.search_engine.lucene.LuceneConfig;
import it.uniroma3.idd.search_engine.lucene.indexer.IndexingCompleteEvent;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.FloatField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class LuceneSearcher implements ApplicationListener<IndexingCompleteEvent> {

    private final LuceneConfig luceneConfig;
    private IndexSearcher searcher;
    private AcronymManager acronymManager;

    private static final StandardAnalyzer STANDARD_ANALYZER = new StandardAnalyzer();
    private static final SimpleAnalyzer SIMPLE_ANALYZER = new SimpleAnalyzer();
    private static final WhitespaceAnalyzer WHITESPACE_ANALYZER = new WhitespaceAnalyzer();
    private Query query;

    @Autowired
    public LuceneSearcher(LuceneConfig luceneConfig) {
        this.luceneConfig = luceneConfig;
    }

    @Override
    public void onApplicationEvent(@NotNull IndexingCompleteEvent event) {
        initializeSearcher();
    }

    private void initializeSearcher() {
        try {
            Path indexPath = Paths.get(luceneConfig.getIndexDirectory());
            Directory directory = FSDirectory.open(indexPath);
            IndexReader reader = DirectoryReader.open(directory);
            searcher = new IndexSearcher(reader);
        } catch (IOException e) {
            throw new RuntimeException("Error initializing the searcher", e);
        }
    }

    public Set<Document> runQueryDocuments(Map<String, String> filters) throws ParseException, InvalidTokenOffsetsException, IOException {
        if (searcher == null) {
            throw new IllegalStateException("Searcher not initialized");
        }

        BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();

        // Query with all fields
        if (filters.containsKey("allFields")) {
            String[] fields = {"title", "authors", "paragraphs", "articleAbstract"};
            String queryText = filters.get("allFields");

            MultiFieldQueryParser queryParser = new MultiFieldQueryParser(fields, STANDARD_ANALYZER);
            query = queryParser.parse(queryText);
            booleanQuery.add(query, BooleanClause.Occur.SHOULD);

            // Add acronym expansion
            for (String field : fields) {
                PhraseQuery acronymQuery = new PhraseQuery(0, field, AcronymManager.expandAcronym(queryText).split(" "));
                booleanQuery.add(acronymQuery, BooleanClause.Occur.SHOULD);
            }

            // Add fuzzy search
            for (String field : fields) {
                FuzzyQuery fuzzyQuery = new FuzzyQuery(new Term(field, queryText), 2);
                booleanQuery.add(fuzzyQuery, BooleanClause.Occur.SHOULD);

            }
        } else {
            // if all fields is not provided, search in each field separately
            for (Map.Entry<String, String> entry : filters.entrySet()) {
                String field = entry.getKey();
                String value = entry.getValue();
                if (value != null && !value.isEmpty()) {
                    if ("authors".equals(field)) {
                        QueryParser parser = new QueryParser(field, SIMPLE_ANALYZER);
                        query = parser.parse(value);
                        booleanQuery.add(query, BooleanClause.Occur.MUST);
                    } else {
                        // use a phrase query for the other fields
                        TokenStream tokenStream = STANDARD_ANALYZER.tokenStream(field, value);
                        CharTermAttribute charTermAttr = tokenStream.addAttribute(CharTermAttribute.class);

                        try {
                            tokenStream.reset(); // reset the TokenStream

                            PhraseQuery.Builder builder = new PhraseQuery.Builder();

                            while (tokenStream.incrementToken()) {
                                String termText = charTermAttr.toString();
                                builder.add(new Term(field, termText)); // add the term to the PhraseQuery
                            }

                            tokenStream.end();
                            tokenStream.close();

                            builder.setSlop(2);

                            // create the PhraseQuery
                            query = builder.build();
                            booleanQuery.add(query, BooleanClause.Occur.MUST);
                        } catch (IOException e) {
                            throw new RuntimeException("Error tokenizing the phrase", e);
                        }
                    }
                }
            }
        }

        // Check if at least one search field is provided
        if (booleanQuery.build().clauses().isEmpty()) {
            throw new IllegalArgumentException("At least one search field must be provided");
        }

        // Limit the number of results
        int limit = filters.get("limit") != null ? Integer.parseInt(filters.get("limit")) : 10;
        TopDocs topDocs;
        try {
            topDocs = searcher.search(booleanQuery.build(), limit);
        } catch (IOException e) {
            throw new RuntimeException("Error searching the index", e);
        }

        // Retrieve the documents
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

        createSnippet(documents, query);
        return documents;
    }

    public Set<Document> runQueryTables(String query, Integer limit) throws ParseException, InvalidTokenOffsetsException, IOException {
        if (searcher == null) {
            throw new IllegalStateException("Searcher not initialized");
        }

        BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();

        // Query with all fields
        String[] fields = {"caption"};
        MultiFieldQueryParser queryParser = new MultiFieldQueryParser(fields, STANDARD_ANALYZER);
        Query queryText = queryParser.parse(query);
        booleanQuery.add(queryText, BooleanClause.Occur.SHOULD);

        // Add acronym expansion
        for (String field : fields) {
            PhraseQuery acronymQuery = new PhraseQuery(0, field, AcronymManager.expandAcronym(query).split(" "));
            booleanQuery.add(acronymQuery, BooleanClause.Occur.SHOULD);
        }

        // Add fuzzy search
        for (String field : fields) {
            FuzzyQuery fuzzyQuery = new FuzzyQuery(new Term(field, query), 2);
            booleanQuery.add(fuzzyQuery, BooleanClause.Occur.SHOULD);
        }

        // Limit the number of results
        limit = limit != null ? limit : 10;
        TopDocs topDocs;
        try {
            topDocs = searcher.search(booleanQuery.build(), limit);
        } catch (IOException e) {
            throw new RuntimeException("Error searching the index", e);
        }

        // Retrieve the documents
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

        createSnippet(documents, queryText);
        return documents;
    }


    private void createSnippet(Set<Document> documents, Query query) throws InvalidTokenOffsetsException, IOException {

        // Elenco dei campi sui quali evidenziare i risultati
        String[] fields = {"title", "authors", "paragraphs", "articleAbstract"};

        // Create the highlighter with the search query (only once, not per field)
        QueryScorer scorer = new QueryScorer(query);
        Highlighter highlighter = new Highlighter(scorer);
        highlighter.setTextFragmenter(new SimpleFragmenter(150)); // Set fragment length

        // Highlight the results for each document
        for (Document doc : documents) {

            String bestSnippet = null;
            float bestScore = Float.MIN_VALUE;
            String bestField = null;

            // Calculate the snippet for each field and compare the scores
            for (String field : fields) {
                String fieldText = doc.get(field);
                if (fieldText != null) {

                    // Get the highlighted fragment for the field if one exists
                    TokenStream tokenStream = STANDARD_ANALYZER.tokenStream(field, fieldText);
                    String snippet = highlighter.getBestFragment(tokenStream, fieldText);

                    // If the snippet is valid, compare the score of the field
                    if (snippet != null) {
                        float fieldScore = Float.parseFloat(doc.get("score"));
                        System.out.println("Field: " + field + " Score: " + fieldScore);
                        if (fieldScore > bestScore) {
                            bestScore = fieldScore;
                            bestSnippet = snippet;
                            bestField = field; // Store the field that gave the best snippet
                        }
                    }
                }
            }

            // Use the best snippet found (if none found, fallback to the full field text)
            if (bestSnippet == null) {
                // Use the full text of the document as a fallback if no snippet was found
                bestSnippet = doc.get("articleAbstract"); // Or another field, depending on your logic
                bestField = "FieldNotFound";
            }

            // Add the best snippet to the document as metadata
            doc.add(new StoredField("snippet", bestSnippet));
            doc.add(new StoredField("snippetField", bestField));

        }

    }

}


