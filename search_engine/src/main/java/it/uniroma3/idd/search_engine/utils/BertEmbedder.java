package it.uniroma3.idd.search_engine.utils;

import ai.onnxruntime.*;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Component
public class BertEmbedder implements AutoCloseable {

    private final OrtEnvironment env;
    private final OrtSession session;
    private final BertTokenizer tokenizer;


    public BertEmbedder(BertConfig bertConfig, BertTokenizer tokenizer) throws Exception {
        this.tokenizer = Objects.requireNonNull(tokenizer, "Tokenizer cannot be null");

        // Inizializza l'ambiente ONNX
        this.env = OrtEnvironment.getEnvironment();

        // Ottieni il percorso del modello e verifica se esiste
        Path modelFilePath = Paths.get(Objects.requireNonNull(bertConfig.getModelPath(), "Model path cannot be null"));
        if (!Files.exists(modelFilePath)) {
            throw new IllegalArgumentException("Model file does not exist at: " + modelFilePath);
        }

        // Creazione della sessione ONNX
        this.session = env.createSession(modelFilePath.toString(), new OrtSession.SessionOptions());

    }

    public float[] getEmbeddings(String text) throws Exception {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Input text cannot be null or empty");
        }

        // Tokenizza il testo
        Map<String, long[]> tokenized = tokenizer.tokenize(text);
        long[] inputIds = padOrTruncate(tokenized.get("input_ids"), 6); // Target length
        long[] attentionMask = padOrTruncate(new long[inputIds.length], inputIds.length, 1L); // All 1s
        long[] tokenTypeIds = padOrTruncate(new long[inputIds.length], inputIds.length, 0L); // All 0s

        // Prepara il batch
        Map<String, OnnxTensor> inputs = new HashMap<>();
        inputs.put("input_ids", OnnxTensor.createTensor(env, new long[][]{inputIds}));
        inputs.put("attention_mask", OnnxTensor.createTensor(env, new long[][]{attentionMask}));
        inputs.put("input.3", OnnxTensor.createTensor(env, new long[][]{tokenTypeIds}));

        // Esegui inferenza
        try (OrtSession.Result result = session.run(inputs)) {
            Object output = result.get(0).getValue();

            // Gestione del tipo di output
            if (output instanceof float[][]) {
                return normalizeEmbedding(((float[][]) output)[0]);
            } else if (output instanceof float[][][]) {
                return normalizeEmbedding(((float[][][]) output)[0][0]);
            } else {
                throw new RuntimeException("Unexpected output type: " + output.getClass());
            }
        }
    }

    private long[] padOrTruncate(long[] array, int targetLength) {
        return padOrTruncate(array, targetLength, 0L);
    }

    private long[] padOrTruncate(long[] array, int targetLength, long paddingValue) {
        if (array.length == targetLength) {
            return array;
        } else if (array.length > targetLength) {
            return Arrays.copyOf(array, targetLength);
        } else {
            long[] result = new long[targetLength];
            System.arraycopy(array, 0, result, 0, array.length);
            Arrays.fill(result, array.length, targetLength, paddingValue);
            return result;
        }
    }

    public float[] normalizeEmbedding(float[] embedding) {
        double norm = 0.0;
        for (float val : embedding) {
            norm += val * val;
        }
        norm = Math.sqrt(norm);

        // Normalizza ogni valore
        for (int i = 0; i < embedding.length; i++) {
            embedding[i] = (float) (embedding[i] / norm);
        }
        return embedding;
    }


    @Override
    public void close() {
        try {
            if (session != null) {
                session.close();
            }
            if (env != null) {
                env.close();
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while closing ONNX resources", e);
        }
    }
}
