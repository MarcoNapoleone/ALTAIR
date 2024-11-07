package it.uniroma3.idd.HW_2.utils;

import org.springframework.context.ApplicationEvent;

public class IndexingCompleteEvent extends ApplicationEvent {
    public IndexingCompleteEvent(Object source) {
        super(source);
    }
}

