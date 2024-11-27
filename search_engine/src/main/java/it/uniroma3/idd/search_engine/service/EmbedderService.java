package it.uniroma3.idd.search_engine.service;

import it.uniroma3.idd.search_engine.utils.BertEmbedder;
import it.uniroma3.idd.search_engine.utils.BertTokenizer;
import it.uniroma3.idd.search_engine.utils.BertConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmbedderService {

    private final BertEmbedder bertEmbedder;

    // Costruttore che inietta la dipendenza di BertEmbedder
    @Autowired
    public EmbedderService(BertConfig bertConfig, BertTokenizer bertTokenizer) throws Exception {
        this.bertEmbedder = new BertEmbedder(bertConfig, bertTokenizer);
    }

    /**
     * Metodo che restituisce gli embeddings per il testo fornito
     * @param text Il testo da cui generare gli embeddings
     * @return Gli embeddings come array di float
     * @throws Exception Se si verificano errori durante il calcolo degli embeddings
     */
    public float[] getEmbeddings(String text) throws Exception {
        return bertEmbedder.getEmbeddings(text);
    }

    // metodo che calcola la cosine similarity tra due vettori di embeddings a partire di due testi
    public double getcosineSimilarity(String text1, String text2) throws Exception {
        float[] embeddings1 = getEmbeddings(text1);
        float[] embeddings2 = getEmbeddings(text2);
        return cosineSimilarity(embeddings1, embeddings2);
    }

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
     * Metodo per chiudere correttamente la sessione e l'ambiente di ONNX
     * @throws Exception Se si verificano errori nella chiusura della sessione
     */
    public void close() throws Exception {
        bertEmbedder.close();
    }
}
