package com.example.document.application.port.in;

import com.example.document.domain.model.DocumentId;

/**
 * Port entrant : suppression d'un document.
 * TODO — business implementation : suppression logique vs physique a decider.
 */
public interface DeleteDocumentUseCase {

    void delete(DocumentId id);
}
