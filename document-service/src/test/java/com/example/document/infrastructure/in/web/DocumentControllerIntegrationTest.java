package com.example.document.infrastructure.in.web;

import com.example.document.AbstractPostgresContainerTest;
import com.example.document.application.port.out.DocumentStoragePort;
import com.example.document.domain.model.DocumentStatus;
import com.example.document.infrastructure.in.dto.ApiError;
import com.example.document.infrastructure.in.dto.DocumentListResponse;
import com.example.document.infrastructure.in.dto.DocumentResponse;
import com.example.document.infrastructure.out.persistence.DocumentEntity;
import com.example.document.infrastructure.out.persistence.DocumentJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test d'integration de la couche web : le serveur HTTP est reellement
 * demarre (port aleatoire) et la persistance tourne sur un vrai PostgreSQL
 * fourni par Testcontainers.
 * <p>
 * Seul le port sortant de stockage est mocke : faire tourner S3 n'apporte
 * rien ici et rendrait la suite dependante d'un service externe. Tout le
 * reste — controller, mapping HTTP, gestion des erreurs, mapper de
 * persistance, JPA/Hibernate, SQL — est exerce pour de vrai.
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.cloud.config.enabled=false",
                "spring.jpa.hibernate.ddl-auto=create-drop",
                "document.storage.aws-region=eu-north-1",
                "document.storage.aws-s3-bucket=test-bucket"
        })
@DisplayName("DocumentController (integration, Postgres Testcontainers)")
class DocumentControllerIntegrationTest extends AbstractPostgresContainerTest {

    private static final byte[] CONTENT = "contenu du rapport".getBytes(StandardCharsets.UTF_8);

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private DocumentJpaRepository documentJpaRepository;

    @MockBean
    private DocumentStoragePort documentStoragePort;

    /**
     * Reproduit la convention de l'adapter S3 : une cle de stockage unique
     * par fichier. La colonne storage_key porte une contrainte UNIQUE en
     * base, deux uploads ne peuvent donc pas partager la meme cle.
     */
    private static String storageKeyFor(String filename) {
        return "3f2b-" + filename;
    }

    @BeforeEach
    void resetDatabaseAndStubStorage() {
        documentJpaRepository.deleteAll();
        when(documentStoragePort.store(anyString(), any(), anyLong()))
                .thenAnswer(invocation -> storageKeyFor(invocation.getArgument(0)));
    }

    private static HttpEntity<MultiValueMap<String, Object>> multipartRequest(String filename, byte[] content) {
        ByteArrayResource filePart = new ByteArrayResource(content) {
            @Override
            public String getFilename() {
                return filename;
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", filePart);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        return new HttpEntity<>(body, headers);
    }

    private DocumentResponse uploadDocument(String filename) {
        ResponseEntity<DocumentResponse> response = restTemplate.postForEntity(
                "/documents/upload", multipartRequest(filename, CONTENT), DocumentResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        return response.getBody();
    }

    @Test
    @DisplayName("POST /documents/upload : 201, document AVAILABLE, ligne ecrite en base")
    void uploadPersistsDocumentAndReturnsCreated() {
        ResponseEntity<DocumentResponse> response = restTemplate.postForEntity(
                "/documents/upload", multipartRequest("rapport.pdf", CONTENT), DocumentResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        DocumentResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.filename()).isEqualTo("rapport.pdf");
        assertThat(body.size()).isEqualTo(CONTENT.length);
        assertThat(body.status()).isEqualTo(DocumentStatus.AVAILABLE.name());
        assertThat(body.createdAt()).isNotNull();

        Optional<DocumentEntity> persisted = documentJpaRepository.findById(UUID.fromString(body.id()));
        assertThat(persisted).isPresent();
        assertThat(persisted.get().getFilename()).isEqualTo("rapport.pdf");
        assertThat(persisted.get().getStorageKey()).isEqualTo(storageKeyFor("rapport.pdf"));
        assertThat(persisted.get().getStatus()).isEqualTo(DocumentStatus.AVAILABLE.name());

        verify(documentStoragePort).store(eq("rapport.pdf"), any(), anyLong());
    }

    @Test
    @DisplayName("POST /documents/upload : 400 quand le nom de fichier viole un invariant metier")
    void uploadRejectsInvalidFilename() {
        String tooLongFilename = "a".repeat(260) + ".pdf";

        ResponseEntity<ApiError> response = restTemplate.postForEntity(
                "/documents/upload", multipartRequest(tooLongFilename, CONTENT), ApiError.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().message()).isEqualTo("File name must not be longer than 255 characters");

        assertThat(documentJpaRepository.count()).isZero();
    }

    @Test
    @DisplayName("GET /documents/{id} : 200 avec les metadonnees rechargees depuis Postgres")
    void getByIdReturnsPersistedDocument() {
        DocumentResponse uploaded = uploadDocument("contrat.pdf");

        ResponseEntity<DocumentResponse> response = restTemplate.getForEntity(
                "/documents/{id}", DocumentResponse.class, uploaded.id());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(uploaded.id());
        assertThat(response.getBody().filename()).isEqualTo("contrat.pdf");
        assertThat(response.getBody().status()).isEqualTo(DocumentStatus.AVAILABLE.name());
    }

    @Test
    @DisplayName("GET /documents/{id} : 404 quand le document n'existe pas")
    void getByIdReturnsNotFoundForUnknownId() {
        UUID unknownId = UUID.randomUUID();

        ResponseEntity<ApiError> response = restTemplate.getForEntity(
                "/documents/{id}", ApiError.class, unknownId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(404);
        assertThat(response.getBody().message()).contains(unknownId.toString());
    }

    @Test
    @DisplayName("GET /documents/all : 200 avec tous les documents presents en base")
    void listReturnsAllDocuments() {
        DocumentResponse first = uploadDocument("premier.pdf");
        DocumentResponse second = uploadDocument("second.pdf");

        ResponseEntity<DocumentListResponse> response = restTemplate.getForEntity(
                "/documents/all", DocumentListResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().documents())
                .extracting(DocumentResponse::id)
                .containsExactlyInAnyOrder(first.id(), second.id());
    }

    @Test
    @DisplayName("GET /documents/all : 200 avec une liste vide quand la base est vide")
    void listReturnsEmptyListWhenNoDocument() {
        ResponseEntity<DocumentListResponse> response = restTemplate.getForEntity(
                "/documents/all", DocumentListResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().documents()).isEmpty();
    }

    @Test
    @DisplayName("GET /documents/{id}/download : 200, contenu binaire et en-tete Content-Disposition")
    void downloadStreamsFileContent() {
        DocumentResponse uploaded = uploadDocument("rapport.pdf");
        when(documentStoragePort.retrieve(storageKeyFor("rapport.pdf")))
                .thenAnswer(invocation -> new ByteArrayInputStream(CONTENT));

        ResponseEntity<byte[]> response = restTemplate.getForEntity(
                "/documents/{id}/download", byte[].class, uploaded.id());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(CONTENT);
        assertThat(response.getHeaders().getContentType())
                .isEqualTo(MediaType.APPLICATION_OCTET_STREAM);
        assertThat(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION))
                .isEqualTo("attachment; filename=\"rapport.pdf\"");
    }

    @Test
    @DisplayName("GET /documents/{id}/download : 404 quand le document n'existe pas")
    void downloadReturnsNotFoundForUnknownId() {
        ResponseEntity<ApiError> response = restTemplate.getForEntity(
                "/documents/{id}/download", ApiError.class, UUID.randomUUID());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("DELETE /documents/{id} : 204, fichier supprime du storage et ligne supprimee en base")
    void deleteRemovesDocumentFromStorageAndDatabase() {
        DocumentResponse uploaded = uploadDocument("a-supprimer.pdf");

        ResponseEntity<Void> response = restTemplate.exchange(
                "/documents/{id}", HttpMethod.DELETE, null, Void.class, uploaded.id());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(documentJpaRepository.findById(UUID.fromString(uploaded.id()))).isEmpty();

        verify(documentStoragePort).delete(storageKeyFor("a-supprimer.pdf"));
    }

    @Test
    @DisplayName("DELETE /documents/{id} : 404 quand le document n'existe pas")
    void deleteReturnsNotFoundForUnknownId() {
        ResponseEntity<ApiError> response = restTemplate.exchange(
                "/documents/{id}", HttpMethod.DELETE, null, ApiError.class, UUID.randomUUID());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(404);
    }

    @Test
    @DisplayName("Le cycle complet upload -> get -> delete laisse la base vide")
    void fullLifecycleLeavesDatabaseEmpty() {
        DocumentResponse uploaded = uploadDocument("cycle-de-vie.pdf");
        assertThat(documentJpaRepository.count()).isEqualTo(1);

        assertThat(restTemplate.getForEntity("/documents/{id}", DocumentResponse.class, uploaded.id())
                .getStatusCode()).isEqualTo(HttpStatus.OK);

        restTemplate.delete("/documents/{id}", uploaded.id());

        assertThat(documentJpaRepository.count()).isZero();

        ResponseEntity<ApiError> afterDelete = restTemplate.getForEntity(
                "/documents/{id}", ApiError.class, uploaded.id());
        assertThat(afterDelete.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
