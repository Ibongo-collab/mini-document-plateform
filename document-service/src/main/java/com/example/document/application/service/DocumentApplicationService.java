package com.example.document.application.service;

import com.example.document.application.port.in.DeleteDocumentUseCase;
import com.example.document.application.port.in.DownloadDocumentUseCase;
import com.example.document.application.port.in.GetDocumentUseCase;
import com.example.document.application.port.in.ListDocumentsUseCase;
import com.example.document.application.port.in.UploadDocumentUseCase;
import com.example.document.application.port.out.DocumentRepositoryPort;
import com.example.document.application.port.out.DocumentStoragePort;
import com.example.document.domain.exception.DocumentNotFoundException;
import com.example.document.domain.model.Document;
import com.example.document.domain.model.DocumentId;
import com.example.document.domain.model.DocumentName;
import com.example.document.domain.model.DocumentSize;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentApplicationService implements
        UploadDocumentUseCase,
        GetDocumentUseCase,
        ListDocumentsUseCase,
        DownloadDocumentUseCase,
        DeleteDocumentUseCase {

    private final DocumentRepositoryPort documentRepositoryPort;
    private final DocumentStoragePort documentStoragePort;


    @Override
    public Document upload(UploadDocumentCommand command) {
        DocumentName filename = DocumentName.of(command.filename());
        DocumentSize size = DocumentSize.ofBytes(command.sizeInBytes());

        String storageKey = documentStoragePort.store(filename.value(), command.content(), size.bytes());

        Document document = Document.create(filename, size, storageKey);
        document.markAsAvailable();

        return documentRepositoryPort.save(document);
    }

    @Override
    public Document getById(DocumentId id) {
        return documentRepositoryPort.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException(id));
    }

    @Override
    public List<Document> listAll() {
        return documentRepositoryPort.findAll();
    }

    @Override
    public InputStream download(DocumentId id) {
        Document document = documentRepositoryPort.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException(id));
        return documentStoragePort.retrieve(document.storageKey());
    }

    @Override
    public void delete(DocumentId id) {
        Document documentToDelete = documentRepositoryPort.findById(id).orElseThrow(() -> new DocumentNotFoundException(id));
        documentToDelete.delete();
        documentStoragePort.delete(documentToDelete.storageKey());
        documentRepositoryPort.deleteById(documentToDelete);

        log.info("Document deleted: {}", documentToDelete.filename());
    }
}
