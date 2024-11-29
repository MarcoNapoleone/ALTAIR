package it.uniroma3.idd.search_engine.config;

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
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080");
        devServer.setDescription("dev");

        Server productionServer = new Server();
        productionServer.setUrl("https://altair.marconapoleone.me");
        productionServer.setDescription("production");

        Server ailabServer = new Server();
        ailabServer.setUrl("http://192.168.127.11:8080");
        ailabServer.setDescription("ailabServer");

        Contact myContact = new Contact();
        myContact.setName("Giorgio Biancini, Marco Napoleone");
        myContact.setEmail("mar.napoleone3@stud.uniroma3.it");

        Info information = new Info()
                .title("ALTAIR APIs documentation")
                .version("1.0")
                .description("REST endpoints to test ALTAIR APIs")
                .contact(myContact);


        return new OpenAPI().info(information).servers(List.of(devServer, productionServer, ailabServer));
    }
}