package com.example.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entree de l'api-gateway.
 * <p>
 * Responsabilite unique : router les requetes HTTP entrantes vers les
 * microservices en aval (document-service). Aucune logique metier ne doit
 * jamais etre ajoutee ici.
 */
@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
