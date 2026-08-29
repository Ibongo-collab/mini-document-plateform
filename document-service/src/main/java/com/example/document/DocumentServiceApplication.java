package com.example.document;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entree de document-service.
 * <p>
 * Coeur du projet : implemente l'architecture hexagonale + DDD pour la
 * gestion des documents. Utilise Spring MVC (Spring Web), pas WebFlux —
 * le caractere reactif de Spring Cloud Gateway ne doit pas se propager ici.
 */
@SpringBootApplication
public class DocumentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DocumentServiceApplication.class, args);
    }
}
