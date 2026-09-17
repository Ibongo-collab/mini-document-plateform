package com.example.document.application.port.in;

import com.example.document.domain.model.Document;
import com.example.document.domain.model.DocumentId;

/**
 * Port entrant : consultation des metadonnees d'un document.
 */
public interface GetDocumentUseCase {

    Document getById(DocumentId id);
}
