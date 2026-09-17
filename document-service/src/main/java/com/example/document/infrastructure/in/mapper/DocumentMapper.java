package com.example.document.infrastructure.in.mapper;

import com.example.document.domain.model.Document;
import com.example.document.infrastructure.in.dto.DocumentResponse;
import org.springframework.stereotype.Component;

@Component
public class DocumentMapper {

    public DocumentResponse toResponse(Document document) {
        return new DocumentResponse(
                document.id().value().toString(),
                document.filename().toString(),
                document.size().bytes(),
                document.status().name(),
                document.createdAt()
        );
    }
}
