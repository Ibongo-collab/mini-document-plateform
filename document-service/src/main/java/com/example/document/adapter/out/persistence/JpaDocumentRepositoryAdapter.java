package com.example.document.adapter.out.persistence;

import com.example.document.application.port.out.DocumentRepositoryPort;
import com.example.document.domain.model.Document;
import com.example.document.domain.model.DocumentId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adapter sortant : implemente DocumentRepositoryPort au-dessus de
 * Spring Data JPA / PostgreSQL.
 * <p>
 * NE PAS implementer la persistance ici : structure preparee uniquement,
 * a completer par l'auteur du projet.
 * TODO — business implementation.
 */
@Component
public class JpaDocumentRepositoryAdapter implements DocumentRepositoryPort {

    private final DocumentJpaRepository documentJpaRepository;
    private final DocumentPersistenceMapper documentPersistenceMapper;

    public JpaDocumentRepositoryAdapter(DocumentJpaRepository documentJpaRepository,
                                         DocumentPersistenceMapper documentPersistenceMapper) {
        this.documentJpaRepository = documentJpaRepository;
        this.documentPersistenceMapper = documentPersistenceMapper;
    }

    @Override
    public Document save(Document document) {
        throw new UnsupportedOperationException("TODO — business implementation");
    }

    @Override
    public Optional<Document> findById(DocumentId id) {
        throw new UnsupportedOperationException("TODO — business implementation");
    }

    @Override
    public List<Document> findAll() {
        throw new UnsupportedOperationException("TODO — business implementation");
    }

    @Override
    public void deleteById(DocumentId id) {
        throw new UnsupportedOperationException("TODO — business implementation");
    }
}
