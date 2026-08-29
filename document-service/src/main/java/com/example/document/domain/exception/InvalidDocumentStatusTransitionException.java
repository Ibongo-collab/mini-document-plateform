package com.example.document.domain.exception;

import com.example.document.domain.model.DocumentStatus;

/**
 * Leve lorsqu'une transition de statut interdite est tentee sur un Document
 * (ex : passer de DELETED a AVAILABLE).
 * TODO — business implementation : a lever depuis l'Aggregate Root une fois
 * les transitions de statut valides definies.
 */
public class InvalidDocumentStatusTransitionException extends DocumentException {

    public InvalidDocumentStatusTransitionException(DocumentStatus from, DocumentStatus to) {
        super("Invalid document status transition: " + from + " -> " + to);
    }
}
