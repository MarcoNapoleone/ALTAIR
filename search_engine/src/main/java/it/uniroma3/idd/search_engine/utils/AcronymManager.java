package it.uniroma3.idd.search_engine.utils;

import java.util.HashMap;
import java.util.Map;

public class AcronymManager {

    private static final Map<String, String> acronymMap = new HashMap<>();

    static {
        acronymMap.put("AI", "Artificial Intelligence");
        acronymMap.put("ML", "Machine Learning");
        acronymMap.put("DL", "Deep Learning");
        acronymMap.put("NLP", "Natural Language Processing");
        acronymMap.put("NN", "Neural Networks");
        acronymMap.put("RL", "Reinforcement Learning");
        acronymMap.put("CV", "Computer Vision");
        acronymMap.put("CNN", "Convolutional Neural Networks");
        acronymMap.put("RNN", "Recurrent Neural Networks");
        acronymMap.put("SVM", "Support Vector Machines");
        acronymMap.put("LSTM", "Long Short-Term Memory");
        acronymMap.put("GAN", "Generative Adversarial Networks");
        acronymMap.put("BERT", "Bidirectional Encoder Representations from Transformers");
        acronymMap.put("GPT", "Generative Pre-trained Transformer");
        acronymMap.put("IoT", "Internet of Things");
        acronymMap.put("API", "Application Programming Interface");
        acronymMap.put("JSON", "JavaScript Object Notation");
        acronymMap.put("XML", "eXtensible Markup Language");
        acronymMap.put("SDK", "Software Development Kit");
        acronymMap.put("DBMS", "Database Management System");
        acronymMap.put("SQL", "Structured Query Language");
        acronymMap.put("CRUD", "Create, Read, Update, Delete");
        acronymMap.put("GPU", "Graphics Processing Unit");
        acronymMap.put("CPU", "Central Processing Unit");
        acronymMap.put("RAM", "Random Access Memory");
        acronymMap.put("HDD", "Hard Disk Drive");
        acronymMap.put("SSD", "Solid State Drive");
        acronymMap.put("DDoS", "Distributed Denial of Service");
        acronymMap.put("XSS", "Cross-Site Scripting");
        acronymMap.put("SQLi", "SQL Injection");
        acronymMap.put("F1 Score", "Harmonic mean of precision and recall");
        acronymMap.put("AUC", "Area Under Curve");
        acronymMap.put("ROC", "Receiver Operating Characteristic");
        acronymMap.put("TPU", "Tensor Processing Unit");
        acronymMap.put("PaaS", "Platform as a Service");
        acronymMap.put("SaaS", "Software as a Service");
        acronymMap.put("IaaS", "Infrastructure as a Service");
        acronymMap.put("VPC", "Virtual Private Cloud");
        acronymMap.put("DNS", "Domain Name System");
        acronymMap.put("HTTP", "Hypertext Transfer Protocol");
        acronymMap.put("HTTPS", "Hypertext Transfer Protocol Secure");
        acronymMap.put("URL", "Uniform Resource Locator");
        acronymMap.put("FTP", "File Transfer Protocol");
        acronymMap.put("SSH", "Secure Shell");
        acronymMap.put("VPN", "Virtual Private Network");
        acronymMap.put("AIoT", "Artificial Intelligence of Things");
        acronymMap.put("LORA", "Long Range");
        acronymMap.put("MLops", "Machine Learning Operations");
        acronymMap.put("ETL", "Extract, Transform, Load");
        acronymMap.put("HCI", "Human-Computer Interaction");
        acronymMap.put("UX/UI", "User Experience / User Interface");
        acronymMap.put("OCR", "Optical Character Recognition");
        acronymMap.put("JSON-LD", "JavaScript Object Notation for Linked Data");
        acronymMap.put("TLS", "Transport Layer Security");
        acronymMap.put("SSL", "Secure Sockets Layer");
        acronymMap.put("RPA", "Robotic Process Automation");
    }


    public static boolean isAcronym(String term) {
        return acronymMap.containsKey(term);
    }


    // Metodo per sostituire gli acronimi nel testo
    public static String replaceAcronyms(String text) {
        return expandAcronym(text);
    }

    // Metodo per espandere la query con acronimi
    public static String expandAcronym(String queryText) {
        if (queryText == null || queryText.trim().isEmpty()) {
            return queryText;  // Restituisce la query originale se è null o vuota
        }

        // Se la query contiene un acronimo, restituisci la forma completa
        String acronym = getAcronym(queryText);
        if (acronym != null) {
            String fullForm = acronymMap.get(acronym);
            if (fullForm != null) {
                return fullForm;
            }
            return queryText;
        }

        // Se la query contiene una forma completa, aggiungi l'acronimo
        String fullForm = getAcronymFull(queryText);
        if (fullForm != null) {
            String acronymFromFullForm = getAcronym(fullForm);
            if (acronymFromFullForm != null) {
                queryText = queryText.replace(fullForm,acronymFromFullForm);
            }
            return queryText;
        }

        return queryText;
    }

    // Verifica se la frase contiene un acronimo
    private static String getAcronym(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;  // Restituisce null se il testo è vuoto o null
        }

        for (String word : text.toUpperCase().split(" ")) {
            if (isAcronym(word)) {
                return word;
            }
        }
        return null;  // Se non troviamo alcun acronimo
    }

    // Verifica se la frase contiene la forma completa di un acronimo
    private static String getAcronymFull(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;  // Restituisce null se il testo è vuoto o null
        }

        // Scorri ogni acronimo e verifica se il nome completo è nel testo
        for (Map.Entry<String, String> entry : acronymMap.entrySet()) {
            String fullName = entry.getValue();
            String acronym = entry.getKey();

            // Controlla se la sequenza di parole completa (nome completo) è nel testo
            if (text.toUpperCase().contains(fullName.toUpperCase())) {
                return fullName;  // Restituisce la forma completa, preservando il caso originale
            }
        }

        return null;  // Se nessun acronimo con nome completo è trovato
    }




}
