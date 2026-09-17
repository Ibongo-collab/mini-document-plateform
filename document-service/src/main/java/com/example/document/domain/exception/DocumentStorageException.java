package com.example.document.domain.exception;

/**
 * Leve lorsqu'une operation de stockage (S3 ou autre) echoue.
 */
public class DocumentStorageException extends DocumentException {

    public DocumentStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
