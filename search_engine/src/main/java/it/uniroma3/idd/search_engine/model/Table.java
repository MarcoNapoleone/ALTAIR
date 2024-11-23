package it.uniroma3.idd.search_engine.model;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;

import java.util.List;

public class Table {
    @Id
    @NotNull
    private String id;               // The ID of the article

    @NotNull
    private String caption;            // The title of the article

}
