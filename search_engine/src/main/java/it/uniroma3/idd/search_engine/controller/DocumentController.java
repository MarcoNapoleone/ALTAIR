package it.uniroma3.idd.search_engine.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.uniroma3.idd.search_engine.dto.GetDocumentResponse;
import it.uniroma3.idd.search_engine.dto.GetDocumentsResponse;
import jakarta.validation.constraints.Null;
import org.apache.lucene.document.Document;
import it.uniroma3.idd.search_engine.service.DocumentService;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/documents")
@Tag(name = "Documents", description = "Operations for retrieving documents by ID or search criteria")
@Validated
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @GetMapping("/{id}")
    @Operation(summary = "Retrieve a document by ID", description = "Returns a document by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Document retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Document not found")
    })
    public Document getDocument(
            @Parameter(description = "ID of the document to retrieve", required = true)
            @PathVariable Long id
    ) {
        return documentService.getDocument(id);
    }

    @GetMapping("/search")
    @Operation(summary = "Search documents", description = "Search for documents by query with optional field filters.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Documents retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid search parameters")
    })
    public GetDocumentsResponse searchDocuments(
            @RequestParam(required = false) @Null @Parameter(description = "Search all the indexes") String query,
            @RequestParam(required = false) @Null @Parameter(description = "Query string to search for") String title,
            @RequestParam(required = false) @Null @Parameter(description = "Query string to search for") String authors,
            @RequestParam(required = false) @Null @Parameter(description = "Query string to search for") String articleAbstract,
            @RequestParam(required = false) @Parameter(description = "Number of documents to retrieve") Integer limit
    ) throws IOException, ParseException, InvalidTokenOffsetsException {

        // if all the fields are null, return error
        if (query == null && title == null && authors == null && articleAbstract == null) {
            throw new IllegalArgumentException("At least one search field must be provided");
        }

        Map<String, String> filters = new HashMap<>();
        if (query != null) filters.put("allFields", query);
        if (title != null) filters.put("title", title);
        if (authors != null) filters.put("authors", authors);
        if (articleAbstract != null) filters.put("articleAbstract", articleAbstract);
        if (limit != null) filters.put("limit", String.valueOf(limit));


        Collection<Document> documents = documentService.getDocumentsQuery(filters);

        Collection<GetDocumentResponse> documentResponses =
                documents
                        .stream()
                        .map(d -> new GetDocumentResponse().documentToGetDocumentResponse(d))
                        .collect(Collectors.toList());

        return new GetDocumentsResponse(documentResponses);


 }
}
