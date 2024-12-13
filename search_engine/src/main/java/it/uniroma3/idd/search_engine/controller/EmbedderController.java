package it.uniroma3.idd.search_engine.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.uniroma3.idd.search_engine.service.EmbedderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/embeddings")
public class EmbedderController {

    @Autowired
    private EmbedderService embedderService;

    /**
     * Endpoint per ottenere gli embeddings di un testo
     * @param text Il testo da elaborare
     * @return Gli embeddings in formato JSON
     */
    @PostMapping("/embeddings")
    @Operation(summary = "test Embedding", description = "Returns the embeddings of the input text using the specified model.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Embeddings retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters")
    })
    public float[] getEmbeddings(
            @RequestParam String text){
        return embedderService.getEmbeddingsAllMini(text);
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
    public double getcosineSimilarity(@RequestParam String text1, @RequestParam String text2)  {

        return embedderService.getCosineSimilarity(text1, text2);
    }
}
