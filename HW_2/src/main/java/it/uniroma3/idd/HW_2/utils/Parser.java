package it.uniroma3.idd.HW_2.utils;

import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;

import it.uniroma3.idd.HW_2.model.Article;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class Parser {

    public static List<Article> articleParser(){
        // Open the directory where all HTML files are stored
        File dir = new File("HW_2/articles");

        // List all files in the directory with .html extension
        File[] files = dir.listFiles((dir1, name) -> name.endsWith(".html"));

        // List to store all article data
        List<Article> articles = new ArrayList<>();

        // If there are HTML files
        if (files != null) {
            // Iterate over all the HTML files
            for (File file : files) {
                try {
                    // Parse the HTML file
                    Document document = Jsoup.parse(file, "UTF-8");

                    // Extract the ID (you can modify this to match your HTML structure)
                    String id = file.getName(); // Or extract from the HTML if necessary

                    // Extract and clean the title (remove HTML tags)
                    String title = document.select("h1").first() != null ? document.select("h1").first().text() : "No Title Found";

                    // Extract authors and clean the text (remove HTML tags)
                    List<String> authors = new ArrayList<>();
                    document.select("span.ltx_personname").forEach(authorElement -> authors.add(authorElement.text()));

                    // Extract the abstract and clean the text (remove HTML tags)
                    String articleAbstract = document.select("p.ltx_p").first() != null ? document.select("p.ltx_p").first().text() : "No Abstract Found";

                    // Extract paragraphs and clean the text (remove HTML tags)
                    List<String> paragraphs = new ArrayList<>();
                    document.select("p.ltx_p").forEach(paragraph -> paragraphs.add(paragraph.text()));

                    // Create an Article object for this document
                    Article article = new Article(id, title, authors, paragraphs, articleAbstract);

                    // Add the article to the list
                    articles.add(article);

                } catch (IOException e) {
                    System.out.println("Error opening the file: " + file.getName());
                    e.printStackTrace();
                }
            }
        }

        return articles;
    }
}
