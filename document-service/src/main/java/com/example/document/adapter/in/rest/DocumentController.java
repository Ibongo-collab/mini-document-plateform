package com.example.document.adapter.in.rest;

import com.example.document.adapter.in.rest.dto.DocumentListResponse;
import com.example.document.adapter.in.rest.dto.DocumentResponse;
import com.example.document.application.port.in.DeleteDocumentUseCase;
import com.example.document.application.port.in.DownloadDocumentUseCase;
import com.example.document.application.port.in.GetDocumentUseCase;
import com.example.document.application.port.in.ListDocumentsUseCase;
import com.example.document.application.port.in.UploadDocumentUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Adapter entrant REST.
 * <p>
 * Depend uniquement des ports entrants (use cases), jamais directement de
 * l'infrastructure (pas de repository JPA, pas de S3 ici). Aucune logique
 * metier ne doit etre ecrite dans cette classe.
 * <p>
 * TODO — business implementation : la conversion Document <-> DTO ainsi que
 * la gestion des codes HTTP fins (404, 400, ...) sont a completer.
 */
@RestController
@RequestMapping("/documents")
public class DocumentController {

    private final UploadDocumentUseCase uploadDocumentUseCase;
    private final GetDocumentUseCase getDocumentUseCase;
    private final ListDocumentsUseCase listDocumentsUseCase;
    private final DownloadDocumentUseCase downloadDocumentUseCase;
    private final DeleteDocumentUseCase deleteDocumentUseCase;

    public DocumentController(UploadDocumentUseCase uploadDocumentUseCase,
                               GetDocumentUseCase getDocumentUseCase,
                               ListDocumentsUseCase listDocumentsUseCase,
                               DownloadDocumentUseCase downloadDocumentUseCase,
                               DeleteDocumentUseCase deleteDocumentUseCase) {
        this.uploadDocumentUseCase = uploadDocumentUseCase;
        this.getDocumentUseCase = getDocumentUseCase;
        this.listDocumentsUseCase = listDocumentsUseCase;
        this.downloadDocumentUseCase = downloadDocumentUseCase;
        this.deleteDocumentUseCase = deleteDocumentUseCase;
    }

    @PostMapping
    public ResponseEntity<DocumentResponse> upload(@RequestParam("file") MultipartFile file) {
        // TODO — business implementation : appeler uploadDocumentUseCase.upload(...)
        // et mapper le Document retourne vers un DocumentResponse.
        throw new UnsupportedOperationException("TODO — business implementation");
    }

    @GetMapping
    public ResponseEntity<DocumentListResponse> list() {
        // TODO — business implementation : appeler listDocumentsUseCase.listAll()
        // et mapper vers DocumentListResponse.
        throw new UnsupportedOperationException("TODO — business implementation");
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponse> getById(@PathVariable String id) {
        // TODO — business implementation
        throw new UnsupportedOperationException("TODO — business implementation");
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<?> download(@PathVariable String id) {
        // TODO — business implementation : retourner le flux binaire avec les
        // bons headers (Content-Disposition, Content-Type...).
        throw new UnsupportedOperationException("TODO — business implementation");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        // TODO — business implementation
        throw new UnsupportedOperationException("TODO — business implementation");
    }
}
