package com.example.document.domain.exception;

/**
 * Leve lorsqu'un Document ou l'un de ses Value Objects viole un invariant metier.
 */
public class DocumentValidationException extends DocumentException {

    public DocumentValidationException(String message) {
        super(message);
    }
}
