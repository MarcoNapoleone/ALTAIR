package it.uniroma3.idd.search_engine.utils;

import it.uniroma3.idd.search_engine.model.Article;
import it.uniroma3.idd.search_engine.model.Table;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class Parser {
    public static List<Article> articleParser() {
        File dir = new File("search_engine/data/articles");
        File[] files = dir.listFiles((dir1, name) -> name.endsWith(".html"));
        List<Article> articles = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                try {
                    Document document = Jsoup.parse(file, "UTF-8");
                    String id = file.getName();
                    String title = document.select("h1").first() != null ? document.select("h1").first().text() : "No Title Found";
                    List<String> authors = new ArrayList<>();
                    document.select("span.ltx_personname").forEach(authorElement -> authors.add(authorElement.text()));
                    String articleAbstract = document.select("p.ltx_p").first() != null ? document.select("p.ltx_p").first().text() : "No Abstract Found";
                    List<String> paragraphs = new ArrayList<>();
                    document.select("p.ltx_p").forEach(paragraph -> paragraphs.add(paragraph.text()));

                    Article article = new Article(id, title, authors, paragraphs, articleAbstract);
                    articles.add(article);

                } catch (IOException e) {
                    System.out.println("Error opening the file: " + file.getName());
                    e.printStackTrace();
                }
            }
        }

        return articles;
    }

    public static List<Table> tableParser() {
        File dir = new File("search_engine/data/tables");
        File[] files = dir.listFiles();
        List<Table> tables = new ArrayList<>();

        //print the number of files in the directory
        System.out.println("Number of files in the directory: " + files.length);

        if (files != null) {
            for (File file : files) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(file);

                    jsonNode.fields().forEachRemaining(entry -> {
                        String id = entry.getKey();
                        JsonNode tableNode = entry.getValue().get("table");
                        String tableHtml = tableNode != null ? tableNode.asText("") : "";

                        // Controlla che il nodo "caption" esista prima di chiamare asText
                        String caption = entry.getValue().has("caption")
                                ? entry.getValue().get("caption").asText("")
                                : "";

                        // Gestisci il nodo "footnotes"
                        List<String> footnotes = new ArrayList<>();
                        if (entry.getValue().has("footnotes")) {
                            entry.getValue().get("footnotes").forEach(footnote -> footnotes.add(footnote.asText()));
                        }

                        // Gestisci il nodo "references"
                        List<String> references = new ArrayList<>();
                        if (entry.getValue().has("references")) {
                            entry.getValue().get("references").forEach(reference -> references.add(reference.asText()));
                        }

                        Table table = new Table(id, caption, tableHtml, footnotes, references);
                        tables.add(table);
                    });


                } catch (IOException e) {
                    System.out.println("Error opening the file: " + file.getName());
                    e.printStackTrace();
                }
            }
        }

        return tables;
    }
}