package com.example.document.application.port.in;

import com.example.document.domain.model.DocumentId;

import java.io.InputStream;

/**
 * Port entrant : telechargement du contenu binaire d'un document.
 * TODO — business implementation : ajuster la signature (streaming,
 * metadonnees associees, etc.) selon les besoins reels.
 */
public interface DownloadDocumentUseCase {

    InputStream download(DocumentId id);
}
