package it.uniroma3.idd.search_engine.utils.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class LuceneConfig {

    @Value("${lucene.queryExplain}")
    private boolean queryExplain;

    @Value("${lucene.index.directory}")
    private String indexDirectory;

    @Value("${lucene.index.initialize}")
    private boolean shouldInitializeIndex;

    public boolean isQueryExplain() {
        return queryExplain;
    }

    public String getIndexDirectory() {
        return indexDirectory;
    }

    public boolean isShouldInitializeIndex() {
        return shouldInitializeIndex;
    }

    @Bean
    public Analyzer customAnalyzer() {
        return new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String fieldName) {
                WhitespaceTokenizer tokenizer = new WhitespaceTokenizer();
                TokenStream filter = new PorterStemFilter(tokenizer);
                return new TokenStreamComponents(tokenizer, filter);
            }
        };
    }
}