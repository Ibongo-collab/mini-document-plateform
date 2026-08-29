package com.example.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Point d'entree du Config Server.
 * <p>
 * Sert les fichiers de configuration centralisee (application.yml,
 * document-service.yml, api-gateway.yml) depuis le repository Git
 * config-repository.
 */
@EnableConfigServer
@SpringBootApplication
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
