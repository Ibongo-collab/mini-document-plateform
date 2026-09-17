package com.example.document.application.port.in;

import com.example.document.domain.model.Document;

import java.io.InputStream;

/**
 * Port entrant : upload d'un nouveau document.
 */
public interface UploadDocumentUseCase {

    Document upload(UploadDocumentCommand command);

    record UploadDocumentCommand(
            String filename,
            long sizeInBytes,
            InputStream content)
    {
    }
}
