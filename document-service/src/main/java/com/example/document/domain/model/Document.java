package com.example.document.domain.model;

import com.example.document.domain.exception.InvalidDocumentStatusTransitionException;

import java.time.Instant;
import java.util.Objects;

/**
 * Aggregate Root du contexte "Document".
 */
public class Document {

    private final DocumentId id;
    private final DocumentName filename;
    private final DocumentSize size;
    private final String storageKey;
    private DocumentStatus status;
    private final Instant createdAt;

    protected Document(DocumentId id,
                       DocumentName filename,
                       DocumentSize size,
                       String storageKey,
                       DocumentStatus status,
                       Instant createdAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.filename = Objects.requireNonNull(filename, "filename must not be null");
        this.size = Objects.requireNonNull(size, "size must not be null");
        this.storageKey = Objects.requireNonNull(storageKey, "storageKey must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
    }

    /**
     * Cree un nouveau Document. Statut initial : UPLOADING, car au moment de
     * la creation de l'agrégat, le fichier vient d'être envoyé au storage
     * mais n'est pas encore confirmé disponible (voir markAsAvailable()).
     */
    public static Document create(DocumentName filename, DocumentSize size, String storageKey) {
        return new Document(
                DocumentId.newId(),
                filename,
                size,
                storageKey,
                DocumentStatus.UPLOADING,
                Instant.now()
        );
    }

    /**
     * Reconstruit un Document existant depuis la persistance.
     * Pas de validation de transition ici : la donnee vient d'une source
     * deja consideree fiable (la base de donnees).
     */
    public static Document reconstruct(DocumentId id,
                                       DocumentName filename,
                                       DocumentSize size,
                                       String storageKey,
                                       DocumentStatus status,
                                       Instant createdAt) {
        return new Document(id, filename, size, storageKey, status, createdAt);
    }

    /**
     * Marque le document comme disponible une fois l'upload confirme.
     * Seule transition valide : UPLOADING -> AVAILABLE.
     */
    public void markAsAvailable() {
        if (status != DocumentStatus.UPLOADING) {
            throw new InvalidDocumentStatusTransitionException(status, DocumentStatus.AVAILABLE);
        }
        this.status = DocumentStatus.AVAILABLE;
    }

    /**
     * Marque le document comme supprimé.
     * Un document deja supprime ne peut pas etre supprime a nouveau.
     */
    public void delete() {
        if (status == DocumentStatus.DELETED) {
            throw new InvalidDocumentStatusTransitionException(status, DocumentStatus.DELETED);
        }
        this.status = DocumentStatus.DELETED;
    }

    public DocumentId id() {
        return id;
    }

    public DocumentName filename() {
        return filename;
    }

    public DocumentSize size() {
        return size;
    }

    public String storageKey() {
        return storageKey;
    }

    public DocumentStatus status() {
        return status;
    }

    public Instant createdAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Document that)) return false;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
