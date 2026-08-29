package com.example.document.domain.exception;

/**
 * Leve lorsqu'un Document ou l'un de ses Value Objects viole un invariant metier.
 * TODO — business implementation : a lever depuis les Value Objects /
 * l'Aggregate Root une fois les regles de validation ecrites.
 */
public class DocumentValidationException extends DocumentException {

    public DocumentValidationException(String message) {
        super(message);
    }
}
