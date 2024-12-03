package it.uniroma3.idd.search_engine.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Applica CORS a tutti gli endpoint
                               .allowedOrigins("http://localhost", "https://altair.marconapoleone.me", "https://altair-search.web.app") // Domini consentiti
                               .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Metodi consentiti
                               .allowedHeaders("*"); // Intestazioni consentite
    }
}
