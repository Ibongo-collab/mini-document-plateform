package com.example.document.domain.exception;

/**
 * Leve lorsqu'une operation de stockage (S3 ou autre) echoue.
 * <p>
 * Cette exception appartient au domaine (le concept "le stockage a echoue"
 * est metier), meme si son implementation concrete (S3DocumentStorageAdapter)
 * appartient a l'infrastructure.
 * TODO — business implementation.
 */
public class DocumentStorageException extends DocumentException {

    public DocumentStorageException(String message) {
        super(message);
    }

    public DocumentStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
