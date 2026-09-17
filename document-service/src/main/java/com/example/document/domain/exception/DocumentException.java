package com.example.document.domain.exception;

/**
 * Racine des exceptions metier du contexte "Document".
 * <p>
 * Java pur — aucune dependance framework (pas de @ResponseStatus ici :
 * la traduction HTTP se fait dans l'adapter REST, pas dans le domaine).
 */
public class DocumentException extends RuntimeException {

    public DocumentException(String message) {
        super(message);
    }

    protected DocumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
