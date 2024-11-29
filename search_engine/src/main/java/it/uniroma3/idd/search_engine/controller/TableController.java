package it.uniroma3.idd.search_engine.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.uniroma3.idd.search_engine.dto.GetDocumentResponse;
import it.uniroma3.idd.search_engine.dto.GetDocumentsResponse;
import it.uniroma3.idd.search_engine.dto.GetTableResponse;
import it.uniroma3.idd.search_engine.dto.GetTablesResponse;
import it.uniroma3.idd.search_engine.service.TableService;
import jakarta.validation.constraints.Null;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tables")
@Tag(name = "Tables", description = "Operations for retrieving tables by ID or search criteria")
@Validated
public class TableController {

    @Autowired
    private TableService tableService;

    // tables search
    @GetMapping("/search")
    @Operation(summary = "Search Tables", description = "Search for tables by query with optional field filters.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Documents retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid search parameters")
    })
    public GetTablesResponse searchDocumentsTables(
            @RequestParam(required = true) @Parameter(description = "Search all the indexes") String query,
            @RequestParam(required = false) @Parameter(description = "Use NLP search") Boolean NLP,
            @RequestParam(required = false) @Parameter(description = "Number of tables to retrieve") Integer limit
    ) throws IOException, ParseException, InvalidTokenOffsetsException {

        // if all the fields are null, return error
        if (query == null) {
            throw new IllegalArgumentException("Query field must not be null");
        }

        if (NLP == null) {
            NLP = false;
        }

        Collection<Document> documents = tableService.getTablesQuery(query, NLP, limit, false);

        Collection<GetTableResponse> tableResponses =
                documents
                        .stream()
                        .map(t -> new GetTableResponse().tableToGetTableResponse(t))
                        .collect(Collectors.toList());

        return new GetTablesResponse(tableResponses);

    }


    @GetMapping("/searchEmbedding")
    @Operation(summary = "Search Tables with Embedding", description = "Search for tables by query with optional field filters.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Documents retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid search parameters")
    })
    public GetTablesResponse searchDocumentsTablesEmbedding(
            @RequestParam(required = true) @Parameter(description = "Search all the indexes") String query,
            @RequestParam(required = false) @Parameter(description = "Number of tables to retrieve") Integer limit
    ) throws IOException, ParseException, InvalidTokenOffsetsException {

        // if all the fields are null, return error
        if (query == null) {
            throw new IllegalArgumentException("Query field must not be null");
        }

        Collection<Document> documents = tableService.getTablesQuery(query, false, limit, true);

        Collection<GetTableResponse> tableResponses =
                documents
                        .stream()
                        .map(t -> new GetTableResponse().tableToGetTableResponse(t))
                        .collect(Collectors.toList());

        return new GetTablesResponse(tableResponses);

    }

}
