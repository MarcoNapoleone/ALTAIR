package it.uniroma3.idd.search_engine.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.uniroma3.idd.search_engine.dto.GetDocumentResponse;
import it.uniroma3.idd.search_engine.dto.GetDocumentsResponse;
import it.uniroma3.idd.search_engine.service.DocumentService;
import jakarta.validation.constraints.Null;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/articles")
@Tag(name = "Articles", description = "Operations for retrieving articles by ID or search criteria")
@Validated
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @GetMapping("/{id}")
    @Operation(summary = "Retrieve a article by ID", description = "Returns a article by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Article retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Article not found")
    })
    public Document getDocument(
            @Parameter(description = "ID of the article to retrieve", required = true)
            @PathVariable Long id
    ) {
        return documentService.getDocument(id);
    }

    // articles search
    @GetMapping("/search/")
    @Operation(summary = "Search documents", description = "Search for articles by query with optional field filters.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Articles retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid search parameters")
    })
    public GetDocumentsResponse searchDocumentsUrls(
            @RequestParam(required = false) @Null @Parameter(description = "Search all the indexes") String query,
            @RequestParam(required = false) @Null @Parameter(description = "Query string to search for") String title,
            @RequestParam(required = false) @Null @Parameter(description = "Query string to search for") String authors,
            @RequestParam(required = false) @Null @Parameter(description = "Query string to search for") String articleAbstract,
            @RequestParam(required = false) @Parameter(description = "Number of articles to retrieve") Integer limit,
            @RequestParam(required = false) @Parameter(description = "Edit Threshold Multiplier") Float tresholdMultiplier
    ) throws IOException, InvalidTokenOffsetsException, ParseException {

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


        Collection<Document> documents = documentService.getDocumentsQuery(filters, tresholdMultiplier);

        //print len of documents
        System.out.println("len of documents: " + documents.size());

        Collection<GetDocumentResponse> documentResponses =
                documents
                        .stream()
                        .map(d -> new GetDocumentResponse().documentToGetDocumentResponse(d))
                        .collect(Collectors.toList());

        return new GetDocumentsResponse(documentResponses);


    }

}