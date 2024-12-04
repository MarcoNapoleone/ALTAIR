package it.uniroma3.idd.search_engine.lucene.searcher;


import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import it.uniroma3.idd.search_engine.lucene.AcronymManager;
import it.uniroma3.idd.search_engine.lucene.LuceneConfig;
import it.uniroma3.idd.search_engine.utils.QueryParsingException;
import it.uniroma3.idd.search_engine.utils.bert.BertEmbedder;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.util.Map;


@Component
public class QueryBuilder {

    private static final StandardAnalyzer STANDARD_ANALYZER = new StandardAnalyzer();
    private static final SimpleAnalyzer SIMPLE_ANALYZER = new SimpleAnalyzer();
    private static BertEmbedder bertEmbedder;
    private static AllMiniLmL6V2EmbeddingModel allMiniLmL6V2EmbeddingModel;

    public QueryBuilder(LuceneConfig luceneconfig, BertEmbedder bertEmbedder) {
        if(luceneconfig.getEmbeddingModel().equalsIgnoreCase("bert")) {
            this.bertEmbedder = bertEmbedder;
            allMiniLmL6V2EmbeddingModel = null;

        } else if (luceneconfig.getEmbeddingModel().equalsIgnoreCase("allmini")) {
            allMiniLmL6V2EmbeddingModel = new AllMiniLmL6V2EmbeddingModel();
            this.bertEmbedder = null;
        }

        else{
            throw new RuntimeException("Invalid model: " + luceneconfig.getEmbeddingModel());
        }

    }

    public Query buildArticleQuery(Map<String, String> filters) throws ParseException {
        BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();

        if (filters.containsKey("allFields")) {
            String queryText = filters.get("allFields");
            String[] fields = {"title", "authors", "paragraphs", "articleAbstract"};
            buildAllFieldsQuery(queryText, fields, booleanQuery);
        } else {
            buildFieldSpecificQueries(filters, booleanQuery);
        }

        if (booleanQuery.build().clauses().isEmpty()) {
            throw new IllegalArgumentException("At least one search field must be provided");
        }

        return booleanQuery.build();
    }

    private void buildAllFieldsQuery(String queryText, String[] fields, BooleanQuery.Builder booleanQuery) throws ParseException {
        try {
            MultiFieldQueryParser queryParser = new MultiFieldQueryParser(fields, STANDARD_ANALYZER);
            booleanQuery.add(queryParser.parse(queryText), BooleanClause.Occur.SHOULD);
        } catch (ParseException e) {
            throw new QueryParsingException("Error parsing query: " + queryText);
        }

        // Add acronym expansion
        addAcronymExpansion(queryText, fields, booleanQuery);

        // Add fuzzy search if query has more than 3 characters
        if (queryText.length() > 5)
            addFuzzySearch(queryText, fields, booleanQuery);
    }

    private void addAcronymExpansion(String queryText, String[] fields, BooleanQuery.Builder booleanQuery) {
        for (String field : fields) {
            PhraseQuery acronymQuery = new PhraseQuery(0, field, AcronymManager.expandAcronym(queryText).split(" "));
            booleanQuery.add(acronymQuery, BooleanClause.Occur.SHOULD);
        }
    }

    private void addFuzzySearch(String queryText, String[] fields, BooleanQuery.Builder booleanQuery) {
        for (String field : fields) {
            FuzzyQuery fuzzyQuery = new FuzzyQuery(new Term(field, queryText), 2);
            booleanQuery.add(fuzzyQuery, BooleanClause.Occur.SHOULD);
        }
    }

    private void buildFieldSpecificQueries(Map<String, String> filters, BooleanQuery.Builder booleanQuery) throws ParseException {
        for (Map.Entry<String, String> entry : filters.entrySet()) {
            String field = entry.getKey();
            String value = entry.getValue();
            if (value != null && !value.isEmpty()) {
                if ("authors".equals(field)) {
                    addAuthorQuery(field, value, booleanQuery);
                } else {
                    addPhraseQuery(field, value, booleanQuery);
                }
            }
        }
    }

    private void addAuthorQuery(String field, String value, BooleanQuery.Builder booleanQuery) throws ParseException {
        QueryParser parser = new QueryParser(field, SIMPLE_ANALYZER);
        Query query = parser.parse(value);
        booleanQuery.add(query, BooleanClause.Occur.MUST);
    }

    private void addPhraseQuery(String field, String value, BooleanQuery.Builder booleanQuery) {
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
            Query query = builder.build();
            booleanQuery.add(query, BooleanClause.Occur.MUST);
        } catch (IOException e) {
            throw new RuntimeException("Error tokenizing the phrase", e);
        }
    }

    public Query buildTableQuery(String queryText, boolean useEmbedding, int limit, String model) {
        BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
        String[] fields = {"caption", "body", "footnotes", "references"};
        try{
            MultiFieldQueryParser queryParser = new MultiFieldQueryParser(fields, STANDARD_ANALYZER);
            booleanQuery.add(queryParser.parse(queryText), BooleanClause.Occur.SHOULD);
        } catch (ParseException e){
            throw new QueryParsingException("Error parsing query:" + queryText);
        }

        if (useEmbedding) {
            float[] queryEmbedding;

            if(model.equalsIgnoreCase("bert")) {

                try {
                    queryEmbedding = bertEmbedder.getEmbeddings(queryText.trim().toLowerCase());
                } catch (Exception e) {
                    throw new RuntimeException("Error generating query embeddings", e);
                }
            }

            else if (model.equalsIgnoreCase("allmini")) {
                try {
                    queryEmbedding = allMiniLmL6V2EmbeddingModel.embed(queryText.trim().toLowerCase()).content().vector();
                } catch (Exception e) {
                    throw new RuntimeException("Error generating query embeddings", e);
                }
            }

            else {
                throw new RuntimeException("Invalid model: " + model);
            }

            try{
                Query vectorQuery = new KnnFloatVectorQuery("embedding", queryEmbedding, limit);
                booleanQuery.add(vectorQuery, BooleanClause.Occur.SHOULD);
            } catch (Exception e){
                throw new QueryParsingException("Error parsing query:" + queryText);
            }
        }

        return booleanQuery.build();
    }

    public Query buildTableQuery2(String queryText, boolean useEmbedding, int limit) {
        BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
        String[] fields = {"caption", "body", "footnotes", "references"};
        try{
            MultiFieldQueryParser queryParser = new MultiFieldQueryParser(fields, STANDARD_ANALYZER);
            booleanQuery.add(queryParser.parse(queryText), BooleanClause.Occur.SHOULD);
        } catch (ParseException e){
            throw new QueryParsingException("Error parsing query:" + queryText);
        }

        if (useEmbedding) {
            EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
            float[] queryEmbedding;

            try {
                queryEmbedding = embeddingModel.embed(queryText.trim().toLowerCase()).content().vector();
            } catch (Exception e) {
                throw new RuntimeException("Error generating query embeddings", e);
            }

            try{
                Query vectorQuery = new KnnFloatVectorQuery("embedding", queryEmbedding, limit);
                booleanQuery.add(vectorQuery, BooleanClause.Occur.SHOULD);
            } catch (Exception e){
                throw new QueryParsingException("Error parsing query:" + queryText);
            }
        }

        return booleanQuery.build();
    }
}
