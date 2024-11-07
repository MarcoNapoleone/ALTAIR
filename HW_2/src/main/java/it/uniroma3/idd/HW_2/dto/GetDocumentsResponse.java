package it.uniroma3.idd.HW_2.dto;

import lombok.*;

import java.util.Collection;

@Data @NoArgsConstructor @AllArgsConstructor
public class GetDocumentsResponse {

    private Collection<GetDocumentResponse> documents;

}
