package com.example.document.application.port.in;

import com.example.document.domain.model.Document;

import java.io.InputStream;

/**
 * Port entrant : upload d'un nouveau document.
 * <p>
 * Contrat uniquement — la signature ci-dessous est une proposition minimale.
 * TODO — business implementation : ajuster la signature (Command object,
 * gestion du flux, etc.) selon les besoins reels.
 */
public interface UploadDocumentUseCase {

    Document upload(UploadDocumentCommand command);

    /**
     * Commande minimale. A affiner soi-meme (validation, champs additionnels...).
     */
    record UploadDocumentCommand(String filename, long sizeInBytes, InputStream content) {
    }
}
