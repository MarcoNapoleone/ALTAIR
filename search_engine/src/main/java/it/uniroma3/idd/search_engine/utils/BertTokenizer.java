package it.uniroma3.idd.search_engine.utils;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Component
public class BertTokenizer {

    private static final String VOCAB_FILE_PATH = "model/vocab.txt";
    private static final String TOKEN_CLS = "[CLS]";
    private static final String TOKEN_SEP = "[SEP]";
    private static final String TOKEN_UNK = "[UNK]";
    private Map<String, Integer> vocab;

    /**
     * Carica il vocabolario da un file solo se necessario.
     */
    private synchronized Map<String, Integer> loadVocab() throws IOException {
        if (vocab == null) {
            vocab = new HashMap<>();
            List<String> lines = Files.readAllLines(Path.of(VOCAB_FILE_PATH));
            for (int i = 0; i < lines.size(); i++) {
                vocab.put(lines.get(i), i);
            }
        }
        return vocab;
    }

    /**
     * Tokenizza il testo e genera input per il modello BERT.
     */
    public Map<String, long[]> tokenize(String text) throws IOException {
        loadVocab();

        List<Long> inputIds = new ArrayList<>();
        List<Long> attentionMask = new ArrayList<>();
        List<Long> tokenTypeIds = new ArrayList<>();

        // Aggiungi token speciale [CLS]
        inputIds.add(getTokenId(TOKEN_CLS));
        attentionMask.add(1L);
        tokenTypeIds.add(0L);

        // Tokenizza parole
        String[] words = text.split("\\s+");
        for (String word : words) {
            inputIds.add(getTokenId(word));
            attentionMask.add(1L);
            tokenTypeIds.add(0L);
        }

        // Aggiungi token speciale [SEP]
        inputIds.add(getTokenId(TOKEN_SEP));
        attentionMask.add(1L);
        tokenTypeIds.add(0L);

        // Costruisci la mappa del risultato
        Map<String, long[]> result = new HashMap<>();
        result.put("input_ids", inputIds.stream().mapToLong(Long::longValue).toArray());
        result.put("attention_mask", attentionMask.stream().mapToLong(Long::longValue).toArray());
        result.put("token_type_ids", tokenTypeIds.stream().mapToLong(Long::longValue).toArray());
        return result;
    }

    /**
     * Restituisce l'ID di un token, o un valore predefinito per token sconosciuti.
     */
    private long getTokenId(String token) {
        return vocab.getOrDefault(token, vocab.getOrDefault(TOKEN_UNK, -1));
    }
}
