package it.uniroma3.idd.search_engine.middleware;


// this is a middleware that is used to create a boolean query from a string
// when users send the query they can format the text with ['(', ')', '*', '"', 'AND', 'OR', 'NOT']
// first things first, they system check if the query is formatted, then produce a dictionary that wil handle the syntax


public class BooleanQuery {

    // using a regex check for the syntax
    public static boolean isFormatted(String query) {
        return query.matches("([a-zA-Z0-9]+\\s?)+");
    }

    // this method will parse the query and create a dictionary that will handle the syntax
    public static String parseQuery(String query) {
        return query.replaceAll("\\s+", " ").replaceAll("AND", " AND ").replaceAll("OR", " OR ").replaceAll("NOT", " NOT ");
    }

    // this method will create the boolean query from the dictionary
    public static String createQuery(String query) {
        return query.replaceAll(" AND ", " && ").replaceAll(" OR ", " || ").replaceAll(" NOT ", " !").replaceAll(" ", " ");
    }

    // this method will create the boolean query from the dictionary
    public static String createQuery(String query, String operator) {
        return query.replaceAll(" AND ", " " + operator + " ").replaceAll(" OR ", " " + operator + " ").replaceAll(" NOT ", " !" + operator + " ").replaceAll(" ", " ");
    }

}
