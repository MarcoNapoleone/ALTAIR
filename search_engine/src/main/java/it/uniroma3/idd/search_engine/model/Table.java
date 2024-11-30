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

    @NotNull
    private String bodyCleaned;        // The body of the table cleaned

    private List<String> footnotes;    // The footnotes of the table

    private List<String> references;   // The references of the table

    private String fileName;           // The name of the file

    public Table(String id, String caption, String body, String bodyCleaned, List<String> footnotes, List<String> references, String fileName) {
        this.id = id;
        this.caption = caption;
        this.body = body;
        this.bodyCleaned = bodyCleaned;
        this.footnotes = footnotes;
        this.references = references;
        this.fileName = fileName;
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

    public @NotNull String getBodyCleaned() {
        return bodyCleaned;
    }

    public void setBodyCleaned(@NotNull String bodyCleaned) {
        this.bodyCleaned = bodyCleaned;
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    //if footnotes is null, return empty string
    public String getFootnotesString() {
        if (footnotes == null) {
            return "";
        }
        return String.join(" ", footnotes);
    }

    //if references is null, return empty string
    public String getReferencesString() {
        if (references == null) {
            return "";
        }
        return String.join(" ", references);
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
