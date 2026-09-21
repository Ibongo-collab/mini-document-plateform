package com.example.document.domain.model;

import com.example.document.domain.exception.InvalidDocumentStatusTransitionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Document (Aggregate Root)")
class DocumentTest {

    private static final DocumentName FILENAME = DocumentName.of("rapport.pdf");
    private static final DocumentSize SIZE = DocumentSize.ofBytes(1024);
    private static final String STORAGE_KEY = "storage-key-123";

    @Test
    @DisplayName("create() genere un document au statut UPLOADING")
    void createStartsInUploadingStatus() {
        Document document = Document.create(FILENAME, SIZE, STORAGE_KEY);

        assertThat(document.id()).isNotNull();
        assertThat(document.filename()).isEqualTo(FILENAME);
        assertThat(document.size()).isEqualTo(SIZE);
        assertThat(document.storageKey()).isEqualTo(STORAGE_KEY);
        assertThat(document.status()).isEqualTo(DocumentStatus.UPLOADING);
        assertThat(document.createdAt()).isNotNull();
    }

    @Test
    @DisplayName("markAsAvailable() passe de UPLOADING a AVAILABLE")
    void markAsAvailableTransitionsFromUploadingToAvailable() {
        Document document = Document.create(FILENAME, SIZE, STORAGE_KEY);

        document.markAsAvailable();

        assertThat(document.status()).isEqualTo(DocumentStatus.AVAILABLE);
    }

    @Test
    @DisplayName("markAsAvailable() echoue si le document n'est pas UPLOADING")
    void markAsAvailableFailsWhenNotUploading() {
        Document document = Document.create(FILENAME, SIZE, STORAGE_KEY);
        document.markAsAvailable();

        assertThatThrownBy(document::markAsAvailable)
                .isInstanceOf(InvalidDocumentStatusTransitionException.class);
    }

    @Test
    @DisplayName("delete() marque le document comme DELETED")
    void deleteMarksDocumentAsDeleted() {
        Document document = Document.create(FILENAME, SIZE, STORAGE_KEY);
        document.markAsAvailable();

        document.delete();

        assertThat(document.status()).isEqualTo(DocumentStatus.DELETED);
    }

    @Test
    @DisplayName("delete() echoue si le document est deja supprime")
    void deleteFailsWhenAlreadyDeleted() {
        Document document = Document.create(FILENAME, SIZE, STORAGE_KEY);
        document.delete();

        assertThatThrownBy(document::delete)
                .isInstanceOf(InvalidDocumentStatusTransitionException.class);
    }

    @Test
    @DisplayName("reconstruct() ne valide pas les transitions (rehydratation depuis la persistance)")
    void reconstructDoesNotValidateTransitions() {
        Document document = Document.reconstruct(
                DocumentId.newId(), FILENAME, SIZE, STORAGE_KEY, DocumentStatus.DELETED, Instant.now());

        assertThat(document.status()).isEqualTo(DocumentStatus.DELETED);
    }

    @Test
    @DisplayName("deux documents avec le meme id sont egaux, meme si les autres champs different")
    void equalityIsBasedOnIdOnly() {
        DocumentId id = DocumentId.newId();
        Document a = Document.reconstruct(id, FILENAME, SIZE, STORAGE_KEY, DocumentStatus.AVAILABLE, Instant.now());
        Document b = Document.reconstruct(id, DocumentName.of("autre.pdf"), SIZE, STORAGE_KEY, DocumentStatus.DELETED, Instant.now());

        assertThat(a).isEqualTo(b);
        assertThat(a).hasSameHashCodeAs(b);
    }

    @Test
    @DisplayName("deux documents avec des ids differents ne sont pas egaux")
    void documentsWithDifferentIdsAreNotEqual() {
        Document a = Document.create(FILENAME, SIZE, STORAGE_KEY);
        Document b = Document.create(FILENAME, SIZE, STORAGE_KEY);

        assertThat(a).isNotEqualTo(b);
    }
}
