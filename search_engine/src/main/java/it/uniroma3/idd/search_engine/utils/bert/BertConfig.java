package it.uniroma3.idd.search_engine.utils.bert;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


public class BertConfig {

    @Getter
    @Value("${bert.model.directory}")
    private String modelPath;

    @Getter
    @Value("${bert.vocab.path}")
    private String vocabPath;

}
