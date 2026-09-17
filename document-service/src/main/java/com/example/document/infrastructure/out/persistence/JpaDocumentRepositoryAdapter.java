package com.example.document.infrastructure.out.persistence;

import com.example.document.application.port.out.DocumentRepositoryPort;
import com.example.document.domain.model.Document;
import com.example.document.domain.model.DocumentId;
import com.example.document.infrastructure.out.mapper.DocumentPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JpaDocumentRepositoryAdapter implements DocumentRepositoryPort {

    private final DocumentJpaRepository documentJpaRepository;
    private final DocumentPersistenceMapper mapper;


    @Override
    public Document save(Document document) {
        UUID id = document.id().value();
        DocumentEntity entity = documentJpaRepository.findById(id).orElseGet(() -> mapper.toEntity(
                Document.reconstruct(document.id(),
                        document.filename(),
                        document.size(),
                        document.storageKey(),
                        document.status(),
                        document.createdAt()))
        );

        return mapper.toDomain(documentJpaRepository.save(entity));
    }

    @Override
    public Optional<Document> findById(DocumentId id) {
        return documentJpaRepository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public List<Document> findAll() {
        return documentJpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(Document document) {
        DocumentEntity entity = documentJpaRepository.findById(document.id().value()).orElseGet(() -> mapper.toEntity(
                Document.reconstruct(
                        document.id(),
                        document.filename(),
                        document.size(),
                        document.storageKey(),
                        document.status(),
                        document.createdAt()))
        );
        documentJpaRepository.deleteById(entity.getId());
    }
}
