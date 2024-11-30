package it.uniroma3.idd.search_engine.dto;

import lombok.*;
import org.apache.lucene.document.Document;

import java.util.Collection;
import java.util.Map;

@Data @NoArgsConstructor @AllArgsConstructor
public class GetDocumentResponse {

    private String id;
    private String title;
    private String authors;
    private Map<String, String> results;

    public GetDocumentResponse documentToGetDocumentResponse(Document document) {

        String snippet = document.get("snippet") != null ? document.get("snippet") : "N/A";
        String snippetField = document.get("snippetField") != null ? document.get("snippetField") : "N/A";

        return new GetDocumentResponse(
                document.get("id"),
                document.get("title"),
                document.get("authors"),
                Map.of("snippet", snippet, "snippetField", snippetField)
        );
    }



}
