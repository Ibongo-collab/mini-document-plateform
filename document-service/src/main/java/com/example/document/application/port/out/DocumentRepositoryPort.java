package com.example.document.application.port.out;

import com.example.document.domain.model.Document;
import com.example.document.domain.model.DocumentId;

import java.util.List;
import java.util.Optional;

/**
 * Port sortant : persistance des metadonnees d'un Document.
 * <p>
 * Abstraction vers le futur adapter PostgreSQL / Spring Data JPA
 * (JpaDocumentRepositoryAdapter). Le domaine et l'application n'ont
 * connaissance que de ce contrat.
 * TODO — business implementation : l'implementation concrete est a ecrire
 * dans adapter/out/persistence.
 */
public interface DocumentRepositoryPort {

    Document save(Document document);

    Optional<Document> findById(DocumentId id);

    List<Document> findAll();

    void deleteById(DocumentId id);
}
