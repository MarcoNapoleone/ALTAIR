package it.uniroma3.idd.search_engine.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.uniroma3.idd.search_engine.dto.GetDocumentResponse;
import it.uniroma3.idd.search_engine.dto.GetDocumentsResponse;
import it.uniroma3.idd.search_engine.service.DocumentService;
import it.uniroma3.idd.search_engine.service.EmbedderService;
import jakarta.validation.constraints.Null;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/embeddings")
public class EmbedderController {

    private final EmbedderService embedderService;

    @Autowired
    public EmbedderController(EmbedderService embedderService) {
        this.embedderService = embedderService;
    }

    /**
     * Endpoint per ottenere gli embeddings di un testo
     * @param text Il testo da elaborare
     * @param model Il modello da utilizzare per gli embeddings ("bert" o "allmini")
     * @return Gli embeddings in formato JSON
     * @throws Exception Se si verificano errori durante l'elaborazione
     */
    @PostMapping("/embeddings")
    @Operation(summary = "test Embedding", description = "Returns the embeddings of the input text using the specified model.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Embeddings retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters")
    })
    public float[] getEmbeddings(
            @RequestParam String text,
            @RequestParam(defaultValue = "bert", required = false) String model
    ) throws Exception {
        if ("allmini".equalsIgnoreCase(model)) {
            return embedderService.getEmbeddingsAllMini(text);
        } else if ("bert".equalsIgnoreCase(model)) {
            return embedderService.getEmbeddingsBert(text);
        } else {
            throw new IllegalArgumentException("Invalid model selection. Choose 'bert' or 'allmini'.");
        }
    }



    /**
     * Endpoint per ottenere la cosine similarity tra due testi
     * @param text1 Il primo testo
     * @param text2 Il secondo testo
     * @return La cosine similarity tra i due testi
     * @throws Exception Se si verificano errori durante il calcolo
     */
    @PostMapping("/cosine-similarity")
    @Operation(summary = "Cosine Similarity", description = "Returns the cosine similarity between two texts.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Articles retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid search parameters")
    })
    public double getcosineSimilarity(@RequestParam String text1, @RequestParam String text2, @RequestParam Boolean useBert) throws Exception {
        if(useBert){
            return embedderService.getCosineSimilarity(text1, text2, useBert);
        }
        else return embedderService.getCosineSimilarity(text1, text2, useBert);
    }

    /**
     * Endpoint per chiudere il servizio
     * @throws Exception Se si verificano errori nella chiusura
     */
    @PostMapping("/close")
    public void close() throws Exception {
        embedderService.close();
    }
}
