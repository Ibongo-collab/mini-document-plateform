package com.example.document.domain.exception;

import com.example.document.domain.model.DocumentId;

/**
 * Leve lorsqu'un Document demande n'existe pas.
 * TODO — business implementation : declencher cette exception depuis
 * le service applicatif / les adapters de persistance.
 */
public class DocumentNotFoundException extends DocumentException {

    public DocumentNotFoundException(DocumentId id) {
        super("Document not found: " + id);
    }
}
