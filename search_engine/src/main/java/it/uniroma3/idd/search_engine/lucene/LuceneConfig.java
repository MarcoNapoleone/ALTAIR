package it.uniroma3.idd.search_engine.lucene;

import lombok.Getter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LuceneConfig {

    @Getter
    @Value("${lucene.queryExplain}")
    private boolean queryExplain;

    @Getter
    @Value("${lucene.index.directory}")
    private String indexDirectory;

    @Getter
    @Value("${lucene.index.initialize}")
    private boolean shouldInitializeIndex;


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