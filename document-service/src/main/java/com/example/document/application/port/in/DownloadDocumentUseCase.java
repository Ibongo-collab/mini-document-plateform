package com.example.document.application.port.in;

import com.example.document.domain.model.DocumentId;

import java.io.InputStream;

/**
 * Port entrant : telechargement du contenu binaire d'un document.
 */
public interface DownloadDocumentUseCase {

    InputStream download(DocumentId id);
}
