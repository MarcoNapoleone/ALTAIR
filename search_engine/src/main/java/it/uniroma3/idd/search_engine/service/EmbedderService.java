package it.uniroma3.idd.search_engine.service;

import it.uniroma3.idd.search_engine.lucene.LuceneConfig;
import it.uniroma3.idd.search_engine.utils.bert.BertEmbedder;
import it.uniroma3.idd.search_engine.utils.bert.BertTokenizer;
import it.uniroma3.idd.search_engine.utils.bert.BertConfig;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmbedderService {

    private final BertEmbedder bertEmbedder;
    private LuceneConfig luceneConfig;



    // Constructor injecting the BertEmbedder dependency
    @Autowired
    public EmbedderService(BertConfig bertConfig, BertTokenizer bertTokenizer,LuceneConfig luceneConfig) throws Exception {
        this.bertEmbedder = new BertEmbedder(bertConfig, bertTokenizer);
        this.luceneConfig = luceneConfig;
    }

    /**
     * Method to retrieve embeddings for the given text
     * @param text The text for which embeddings are generated
     * @return The embeddings as a float array
     * @throws Exception If an error occurs during the embedding calculation
     */
    public float[] getEmbeddingsBert(String text) throws Exception {
        return bertEmbedder.getEmbeddings(text);
    }

    public float[] getEmbeddingsAllMini(String text) {
        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        return embeddingModel.embed(text.trim().toLowerCase()).content().vector();
    }

    /**
     * Method to calculate cosine similarity between embeddings of two texts
     * @param text1 First text
     * @param text2 Second text
     * @return The cosine similarity score
     * @throws Exception If an error occurs during embedding computation
     */
    public double getCosineSimilarity(String text1, String text2, Boolean useBert) throws Exception {
        if(useBert) {
            return cosineSimilarity(getEmbeddingsBert(text1), getEmbeddingsBert(text2));
        }
        else {
            return cosineSimilarity(getEmbeddingsAllMini(text1), getEmbeddingsAllMini(text2));
        }
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
