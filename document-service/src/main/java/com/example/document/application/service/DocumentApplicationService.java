package com.example.document.application.service;

import com.example.document.application.port.in.DeleteDocumentUseCase;
import com.example.document.application.port.in.DownloadDocumentUseCase;
import com.example.document.application.port.in.GetDocumentUseCase;
import com.example.document.application.port.in.ListDocumentsUseCase;
import com.example.document.application.port.in.UploadDocumentUseCase;
import com.example.document.application.port.out.DocumentRepositoryPort;
import com.example.document.application.port.out.DocumentStoragePort;
import com.example.document.domain.model.Document;
import com.example.document.domain.model.DocumentId;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

/**
 * Implementation applicative des use cases entrants.
 * <p>
 * Depend uniquement des ports (in et out), jamais directement d'une
 * technologie d'infrastructure. Injection par constructeur — pas de
 * {@code @Autowired} sur les champs.
 * <p>
 * TODO — business implementation : chaque methode ne fait aujourd'hui que
 * declarer l'intention ; la logique reelle (orchestration domaine + ports
 * sortants) est a ecrire par l'auteur du projet.
 */
@Service
public class DocumentApplicationService implements
        UploadDocumentUseCase,
        GetDocumentUseCase,
        ListDocumentsUseCase,
        DownloadDocumentUseCase,
        DeleteDocumentUseCase {

    private final DocumentRepositoryPort documentRepositoryPort;
    private final DocumentStoragePort documentStoragePort;

    public DocumentApplicationService(DocumentRepositoryPort documentRepositoryPort,
                                       DocumentStoragePort documentStoragePort) {
        this.documentRepositoryPort = documentRepositoryPort;
        this.documentStoragePort = documentStoragePort;
    }

    @Override
    public Document upload(UploadDocumentCommand command) {
        throw new UnsupportedOperationException("TODO — business implementation");
    }

    @Override
    public Document getById(DocumentId id) {
        throw new UnsupportedOperationException("TODO — business implementation");
    }

    @Override
    public List<Document> listAll() {
        throw new UnsupportedOperationException("TODO — business implementation");
    }

    @Override
    public InputStream download(DocumentId id) {
        throw new UnsupportedOperationException("TODO — business implementation");
    }

    @Override
    public void delete(DocumentId id) {
        throw new UnsupportedOperationException("TODO — business implementation");
    }
}
