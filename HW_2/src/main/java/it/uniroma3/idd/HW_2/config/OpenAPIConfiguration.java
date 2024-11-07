package it.uniroma3.idd.HW_2.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfiguration {

    @Bean
    public OpenAPI defineOpenApi() {
        Server server = new Server();
        server.setUrl("http://localhost:8080");
        server.setDescription("dev");

        Contact myContact = new Contact();
        myContact.setName("Giorgio Biancini, Marco Napoleone");
        myContact.setEmail("mar.napoleone3@stud.uniroma3.it");

        Info information = new Info()
                .title("IDD HW2")
                .version("1.0")
                .description("REST endpoints to test Lucene framework search engine")
                .contact(myContact);


        return new OpenAPI().info(information).servers(List.of(server));
    }
}