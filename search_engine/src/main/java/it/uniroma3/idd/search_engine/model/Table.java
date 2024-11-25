package it.uniroma3.idd.search_engine.model;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;

import java.util.List;

public class Table {
    @Id
    @NotNull
    private String id;               // The ID of the table

    @NotNull
    private String caption;            // The title of the table

    @NotNull
    private String body;               // The body of the table

    private List<String> footnotes;    // The footnotes of the table

    private List<String> references;   // The references of the table

    public Table(String id, String caption, String body, List<String> footnotes, List<String> references) {
        this.id = id;
        this.caption = caption;
        this.body = body;
        this.footnotes = footnotes;
        this.references = references;
    }

    public @NotNull String getId() {
        return id;
    }

    public void setId(@NotNull String id) {
        this.id = id;
    }

    public @NotNull String getCaption() {
        return caption;
    }

    public void setCaption(@NotNull String caption) {
        this.caption = caption;
    }

    public @NotNull String getBody() {
        return body;
    }

    public void setBody(@NotNull String body) {
        this.body = body;
    }

    public List<String> getFootnotes() {
        return footnotes;
    }

    public void setFootnotes(List<String> footnotes) {
        this.footnotes = footnotes;
    }

    public List<String> getReferences() {
        return references;
    }

    public void setReferences(List<String> references) {
        this.references = references;
    }

    @Override
    public String toString() {
        return "Table{" +
                "id='" + id + '\'' +
                ", caption='" + caption + '\'' +
                ", body='" + body + '\'' +
                ", footnotes=" + footnotes +
                ", references=" + references +
                '}';
    }
}
