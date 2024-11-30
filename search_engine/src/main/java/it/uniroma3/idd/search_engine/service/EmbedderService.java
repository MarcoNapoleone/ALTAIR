package it.uniroma3.idd.search_engine.service;

import it.uniroma3.idd.search_engine.utils.bert.BertEmbedder;
import it.uniroma3.idd.search_engine.utils.bert.BertTokenizer;
import it.uniroma3.idd.search_engine.utils.bert.BertConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmbedderService {

    private final BertEmbedder bertEmbedder;

    // Constructor injecting the BertEmbedder dependency
    @Autowired
    public EmbedderService(BertConfig bertConfig, BertTokenizer bertTokenizer) throws Exception {
        this.bertEmbedder = new BertEmbedder(bertConfig, bertTokenizer);
    }

    /**
     * Method to retrieve embeddings for the given text
     * @param text The text for which embeddings are generated
     * @return The embeddings as a float array
     * @throws Exception If an error occurs during the embedding calculation
     */
    public float[] getEmbeddings(String text) throws Exception {
        return bertEmbedder.getEmbeddings(text);
    }

    /**
     * Method to calculate cosine similarity between embeddings of two texts
     * @param text1 First text
     * @param text2 Second text
     * @return The cosine similarity score
     * @throws Exception If an error occurs during embedding computation
     */
    public double getCosineSimilarity(String text1, String text2) throws Exception {
        float[] embeddings1 = getEmbeddings(text1);
        float[] embeddings2 = getEmbeddings(text2);
        return cosineSimilarity(embeddings1, embeddings2);
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

    /**
     * Method to properly close the ONNX session and environment
     * @throws Exception If an error occurs during session closure
     */
    public void close() throws Exception {
        bertEmbedder.close();
    }
}
