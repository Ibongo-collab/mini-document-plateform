package com.example.document.application.port.out;

import com.example.document.domain.model.Document;
import com.example.document.domain.model.DocumentId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port sortant : persistance des metadonnees d'un Document.
 */
public interface DocumentRepositoryPort {

    Document save(Document document);

    Optional<Document> findById(DocumentId id);

    List<Document> findAll();

    void deleteById(Document document);
}
