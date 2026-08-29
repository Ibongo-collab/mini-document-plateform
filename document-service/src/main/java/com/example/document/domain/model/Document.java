package com.example.document.domain.model;

import java.time.Instant;
import java.util.Objects;

/**
 * Aggregate Root du contexte "Document".
 * <p>
 * Java pur — aucune dependance vers Spring, JPA, AWS ou tout autre
 * framework. C'est ici que doivent vivre les invariants et les regles
 * metier du domaine.
 * <p>
 * TODO — business implementation : cette classe ne contient volontairement
 * aucune logique metier. A implementer soi-meme, par exemple :
 * <ul>
 *     <li>les transitions de statut valides (UPLOADING -> AVAILABLE -> DELETED)</li>
 *     <li>les invariants de coherence (ex : un document DELETED ne peut pas
 *     redevenir AVAILABLE)</li>
 *     <li>les methodes de comportement (markAsAvailable(), delete(), ...)</li>
 * </ul>
 */
public class Document {

    private final DocumentId id;
    private DocumentName filename;
    private DocumentSize size;
    private String storageKey;
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
        this.storageKey = storageKey;
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
    }

    /**
     * Factory method de creation d'un nouveau Document.
     * TODO — business implementation : appliquer les invariants de creation
     * (statut initial, validation des champs, etc.)
     */
    public static Document create(DocumentName filename, DocumentSize size, String storageKey) {
        throw new UnsupportedOperationException("TODO — business implementation");
    }

    /**
     * Reconstruction d'un Document existant (ex : depuis la persistance).
     * TODO — business implementation.
     */
    public static Document reconstruct(DocumentId id,
                                        DocumentName filename,
                                        DocumentSize size,
                                        String storageKey,
                                        DocumentStatus status,
                                        Instant createdAt) {
        throw new UnsupportedOperationException("TODO — business implementation");
    }

    /**
     * Marque le document comme disponible une fois l'upload termine.
     * TODO — business implementation : verifier la transition de statut.
     */
    public void markAsAvailable() {
        throw new UnsupportedOperationException("TODO — business implementation");
    }

    /**
     * Marque le document comme supprime.
     * TODO — business implementation : verifier la transition de statut.
     */
    public void delete() {
        throw new UnsupportedOperationException("TODO — business implementation");
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
