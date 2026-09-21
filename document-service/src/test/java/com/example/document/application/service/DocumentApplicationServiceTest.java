package com.example.document.application.service;

import com.example.document.application.port.in.UploadDocumentUseCase.UploadDocumentCommand;
import com.example.document.application.port.out.DocumentRepositoryPort;
import com.example.document.application.port.out.DocumentStoragePort;
import com.example.document.domain.exception.DocumentNotFoundException;
import com.example.document.domain.model.Document;
import com.example.document.domain.model.DocumentId;
import com.example.document.domain.model.DocumentName;
import com.example.document.domain.model.DocumentSize;
import com.example.document.domain.model.DocumentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Tests unitaires de la couche application : le service est teste seul,
 * avec les deux ports sortants mockes (aucun Spring, aucune base, aucun S3).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DocumentApplicationService")
class DocumentApplicationServiceTest {

    @Mock
    private DocumentRepositoryPort documentRepositoryPort;

    @Mock
    private DocumentStoragePort documentStoragePort;

    private DocumentApplicationService service;

    @BeforeEach
    void setUp() {
        service = new DocumentApplicationService(documentRepositoryPort, documentStoragePort);
    }

    private static Document existingDocument(DocumentId id, String storageKey) {
        return Document.reconstruct(
                id,
                DocumentName.of("existant.pdf"),
                DocumentSize.ofBytes(10),
                storageKey,
                DocumentStatus.AVAILABLE,
                Instant.now());
    }

    @Test
    @DisplayName("upload() stocke le contenu puis persiste le document en AVAILABLE")
    void uploadStoresContentAndPersistsAvailableDocument() {
        InputStream content = new ByteArrayInputStream("hello".getBytes());
        UploadDocumentCommand command = new UploadDocumentCommand("rapport.pdf", 5L, content);

        when(documentStoragePort.store(eq("rapport.pdf"), any(), eq(5L))).thenReturn("storage-key-1");
        when(documentRepositoryPort.save(any(Document.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Document result = service.upload(command);

        assertThat(result.filename().value()).isEqualTo("rapport.pdf");
        assertThat(result.storageKey()).isEqualTo("storage-key-1");
        assertThat(result.status()).isEqualTo(DocumentStatus.AVAILABLE);

        ArgumentCaptor<Document> captor = ArgumentCaptor.forClass(Document.class);
        verify(documentRepositoryPort).save(captor.capture());
        assertThat(captor.getValue().status()).isEqualTo(DocumentStatus.AVAILABLE);
    }

    @Test
    @DisplayName("getById() renvoie le document quand il existe")
    void getByIdReturnsDocumentWhenFound() {
        DocumentId id = DocumentId.newId();
        Document document = existingDocument(id, "key");
        when(documentRepositoryPort.findById(id)).thenReturn(Optional.of(document));

        Document result = service.getById(id);

        assertThat(result).isEqualTo(document);
    }

    @Test
    @DisplayName("getById() leve DocumentNotFoundException quand le document n'existe pas")
    void getByIdThrowsWhenNotFound() {
        DocumentId id = DocumentId.newId();
        when(documentRepositoryPort.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(id))
                .isInstanceOf(DocumentNotFoundException.class);
    }

    @Test
    @DisplayName("listAll() delegue directement au repository")
    void listAllDelegatesToRepository() {
        when(documentRepositoryPort.findAll()).thenReturn(List.of());

        List<Document> result = service.listAll();

        assertThat(result).isEmpty();
        verify(documentRepositoryPort).findAll();
    }

    @Test
    @DisplayName("download() recupere le contenu depuis le storage a partir de la storageKey du document")
    void downloadRetrievesContentFromStorage() {
        DocumentId id = DocumentId.newId();
        Document document = existingDocument(id, "key-42");
        InputStream expectedStream = new ByteArrayInputStream("data".getBytes());
        when(documentRepositoryPort.findById(id)).thenReturn(Optional.of(document));
        when(documentStoragePort.retrieve("key-42")).thenReturn(expectedStream);

        InputStream result = service.download(id);

        assertThat(result).isSameAs(expectedStream);
    }

    @Test
    @DisplayName("download() leve DocumentNotFoundException et ne touche pas au storage quand le document n'existe pas")
    void downloadThrowsWhenNotFound() {
        DocumentId id = DocumentId.newId();
        when(documentRepositoryPort.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.download(id))
                .isInstanceOf(DocumentNotFoundException.class);
        verifyNoInteractions(documentStoragePort);
    }

    @Test
    @DisplayName("delete() supprime du storage puis du repository, et marque le document DELETED")
    void deleteRemovesFromStorageAndRepository() {
        DocumentId id = DocumentId.newId();
        Document document = existingDocument(id, "key-99");
        when(documentRepositoryPort.findById(id)).thenReturn(Optional.of(document));

        service.delete(id);

        verify(documentStoragePort).delete("key-99");
        verify(documentRepositoryPort).deleteById(document);
        assertThat(document.status()).isEqualTo(DocumentStatus.DELETED);
    }

    @Test
    @DisplayName("delete() leve DocumentNotFoundException et ne touche ni au storage ni au repository quand le document n'existe pas")
    void deleteThrowsWhenNotFoundAndDoesNotTouchStorageOrRepository() {
        DocumentId id = DocumentId.newId();
        when(documentRepositoryPort.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(id))
                .isInstanceOf(DocumentNotFoundException.class);

        verify(documentStoragePort, never()).delete(any());
        verify(documentRepositoryPort, never()).deleteById(any());
    }
}
