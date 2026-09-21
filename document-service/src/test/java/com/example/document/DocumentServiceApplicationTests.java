package com.example.document;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest(properties = {
        "spring.cloud.config.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "document.storage.aws-region=eu-north-1",
        "document.storage.aws-s3-bucket=test-bucket"
})
@DisplayName("document-service : demarrage du contexte")
class DocumentServiceApplicationTests extends AbstractPostgresContainerTest {

    @Test
    @DisplayName("le contexte applicatif se charge")
    void contextLoads() {
        // Echoue si un bean est mal cable ou si le schema ne peut pas etre cree.
    }
}
