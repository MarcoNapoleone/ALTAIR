package it.uniroma3.idd.search_engine.model;

import jakarta.validation.constraints.NotNull;
import jdk.jfr.Enabled;
import org.springframework.data.annotation.Id;

import java.util.List;

public class Article {

    @Id
    @NotNull
    private String id;               // The ID of the article

    @NotNull
    private String title;            // The title of the article

    private List<String> authors;    // List of authors
    private List<String> paragraphs; // List of paragraphs
    private String articleAbstract;  // The abstract of the article

    // Constructor
    public Article(String id, String title, List<String> authors, List<String> paragraphs, String articleAbstract) {
        this.id = id;
        this.title = title;
        this.authors = authors;
        this.paragraphs = paragraphs;
        this.articleAbstract = articleAbstract;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public List<String> getParagraphs() {
        return paragraphs;
    }

    public void setParagraphs(List<String> paragraphs) {
        this.paragraphs = paragraphs;
    }

    public String getArticleAbstract() {
        return articleAbstract;
    }

    public void setArticleAbstract(String articleAbstract) {
        this.articleAbstract = articleAbstract;
    }

    // Override toString for easy printing
    @Override
    public String toString() {
        return "Article{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", authors=" + authors +
                ", paragraphs=" + paragraphs +
                '}';
    }
}
