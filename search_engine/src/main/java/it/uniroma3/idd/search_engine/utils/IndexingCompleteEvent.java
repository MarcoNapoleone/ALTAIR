package it.uniroma3.idd.search_engine.utils;

import org.springframework.context.ApplicationEvent;

public class IndexingCompleteEvent extends ApplicationEvent {
    public IndexingCompleteEvent(Object source) {
        super(source);
    }
}

