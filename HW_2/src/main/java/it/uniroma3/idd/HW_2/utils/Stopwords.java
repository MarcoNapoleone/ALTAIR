package it.uniroma3.idd.HW_2.utils;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Stopwords{
    public static List<String> getStopwords() {

        // Percorso del file che contiene le stopwords
        String filePath = "HW_2/stopwords-en.txt";

        // Crea una lista per memorizzare le stopwords
        List<String> stopWordsList = new ArrayList<>();

        // Leggi il file
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Aggiungi la stopword alla lista, rimuovendo eventuali spazi extra
                stopWordsList.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stopWordsList;
    }
}
