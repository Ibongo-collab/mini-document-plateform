package com.example.document.infrastructure.in.dto;

import java.util.List;

/**
 * DTO de sortie representant la liste des documents dans l'API REST.
 * */
public record DocumentListResponse(List<DocumentResponse> documents) {
}
