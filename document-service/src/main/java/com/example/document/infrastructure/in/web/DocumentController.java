package com.example.document.infrastructure.in.web;

import com.example.document.domain.model.Document;
import com.example.document.domain.model.DocumentId;
import com.example.document.infrastructure.in.dto.DocumentListResponse;
import com.example.document.infrastructure.in.dto.DocumentResponse;
import com.example.document.application.port.in.DeleteDocumentUseCase;
import com.example.document.application.port.in.DownloadDocumentUseCase;
import com.example.document.application.port.in.GetDocumentUseCase;
import com.example.document.application.port.in.ListDocumentsUseCase;
import com.example.document.application.port.in.UploadDocumentUseCase;
import com.example.document.infrastructure.in.mapper.DocumentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;



@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final UploadDocumentUseCase uploadDocumentUseCase;
    private final GetDocumentUseCase getDocumentUseCase;
    private final ListDocumentsUseCase listDocumentsUseCase;
    private final DownloadDocumentUseCase downloadDocumentUseCase;
    private final DeleteDocumentUseCase deleteDocumentUseCase;
    private final DocumentMapper mapper;


    @PostMapping("/upload")
    public ResponseEntity<DocumentResponse> upload(@RequestParam("file") MultipartFile file) throws IOException {
        UploadDocumentUseCase.UploadDocumentCommand command = new UploadDocumentUseCase.UploadDocumentCommand(
                file.getOriginalFilename(),
                file.getSize(),
                file.getInputStream()
        );

        Document document = uploadDocumentUseCase.upload(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(document));
    }

    @GetMapping("/all")
    public ResponseEntity<DocumentListResponse> list() {
        List<DocumentResponse> responses = listDocumentsUseCase.listAll().stream()
                .map(mapper::toResponse)
                .toList();
        return ResponseEntity.ok(new DocumentListResponse(responses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponse> getById(@PathVariable("id") UUID id) {
        Document document = getDocumentUseCase.getById(DocumentId.of(id));
        return ResponseEntity.ok(mapper.toResponse(document));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<?> download(@PathVariable("id") UUID id) {
        DocumentId documentId = DocumentId.of(id);
        Document document = getDocumentUseCase.getById(documentId);
        InputStream content = downloadDocumentUseCase.download(documentId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.filename().value() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(content));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
        deleteDocumentUseCase.delete(DocumentId.of(id));
        return ResponseEntity.noContent().build();
    }
}
