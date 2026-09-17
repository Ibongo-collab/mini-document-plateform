package com.example.document.application.port.in;

import com.example.document.domain.model.Document;

import java.util.List;

/**
 * Port entrant : consultation de la liste des documents.
 */
public interface ListDocumentsUseCase {

    List<Document> listAll();
}
