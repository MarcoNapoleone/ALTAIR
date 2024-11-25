package it.uniroma3.idd.search_engine.dto;

import lombok.*;

import java.util.Collection;

@Data @NoArgsConstructor @AllArgsConstructor
public class GetTablesResponse {

    private Collection<GetTableResponse> tables;

}
