package com.example.document.domain.exception;

import com.example.document.domain.model.DocumentId;

public class DocumentNotFoundException extends DocumentException {

    public DocumentNotFoundException(DocumentId id) {
        super("Document not found: %s ".formatted(id));
    }
}
