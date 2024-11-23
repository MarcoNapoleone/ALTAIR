package it.uniroma3.idd.search_engine.lucene.indexer;

import org.springframework.context.ApplicationEvent;

public class IndexingCompleteEvent extends ApplicationEvent {
    public IndexingCompleteEvent(Object source) {
        super(source);
    }
}

