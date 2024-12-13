package it.uniroma3.idd.search_engine.service;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import org.springframework.stereotype.Service;

@Service
public class EmbedderService {


    public float[] getEmbeddingsAllMini(String text) {
        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        return embeddingModel.embed(text.trim().toLowerCase()).content().vector();
    }

    /**
     * Method to calculate cosine similarity between embeddings of two texts
     * @param text1 First text
     * @param text2 Second text
     * @return The cosine similarity score
     */
    public double getCosineSimilarity(String text1, String text2) {
        return cosineSimilarity(getEmbeddingsAllMini(text1), getEmbeddingsAllMini(text2));
    }

    /**
     * Method to calculate cosine similarity between two embedding vectors
     * @param vectorA First embedding vector
     * @param vectorB Second embedding vector
     * @return The cosine similarity score
     */
    public double cosineSimilarity(float[] vectorA, float[] vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += vectorA[i] * vectorA[i];
            normB += vectorB[i] * vectorB[i];
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
