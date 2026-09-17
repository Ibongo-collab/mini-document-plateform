package com.example.document.infrastructure.out.mapper;

import com.example.document.domain.model.*;
import com.example.document.infrastructure.out.persistence.DocumentEntity;
import org.springframework.stereotype.Component;

@Component
public class DocumentPersistenceMapper {

    public Document toDomain(DocumentEntity entity) {
        return Document.reconstruct(
                DocumentId.of(entity.getId()),
                DocumentName.of(entity.getFilename()),
                DocumentSize.ofBytes(entity.getSize()),
                entity.getStorageKey(),
                DocumentStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt()
        );
    }

    public DocumentEntity toEntity(Document document) {
        return new DocumentEntity(
                document.id().value(),
                document.filename().value(),
                document.size().bytes(),
                document.storageKey(),
                document.status().name(),
                document.createdAt()
        );
    }
}
