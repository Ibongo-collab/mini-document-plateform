package com.example.document.adapter.out.persistence;

import com.example.document.domain.model.Document;
import org.springframework.stereotype.Component;

/**
 * Mapper entre l'Aggregate Root du domaine (Document) et l'entite technique
 * de persistance (DocumentEntity).
 * <p>
 * Isole le domaine de tout detail JPA.
 * TODO — business implementation : ecrire la conversion dans les deux sens.
 */
@Component
public class DocumentPersistenceMapper {

    public DocumentEntity toEntity(Document document) {
        throw new UnsupportedOperationException("TODO — business implementation");
    }

    public Document toDomain(DocumentEntity entity) {
        throw new UnsupportedOperationException("TODO — business implementation");
    }
}
