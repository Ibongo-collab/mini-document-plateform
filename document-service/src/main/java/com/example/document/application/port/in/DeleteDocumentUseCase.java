package com.example.document.application.port.in;

import com.example.document.domain.model.DocumentId;


/**
 * Port entrant : suppression d'un document.
 */
public interface DeleteDocumentUseCase {

    void delete(DocumentId id);
}
