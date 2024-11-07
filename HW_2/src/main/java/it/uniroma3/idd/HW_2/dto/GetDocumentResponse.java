package it.uniroma3.idd.HW_2.dto;

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

        return new GetDocumentResponse(
                document.get("id"),
                document.get("title"),
                document.get("authors"),
                Map.of("snippet", document.get("snippet"), "snippetField", document.get("snippetField"))
        );

    }


}
