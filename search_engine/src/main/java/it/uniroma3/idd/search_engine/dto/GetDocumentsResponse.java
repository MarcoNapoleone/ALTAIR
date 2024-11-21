package it.uniroma3.idd.search_engine.dto;

import lombok.*;

import java.util.Collection;

@Data @NoArgsConstructor @AllArgsConstructor
public class GetDocumentsResponse {

    private Collection<GetDocumentResponse> documents;

}
