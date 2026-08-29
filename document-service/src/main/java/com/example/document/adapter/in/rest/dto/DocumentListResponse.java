package com.example.document.adapter.in.rest.dto;

import java.util.List;

/**
 * DTO de sortie representant la liste des documents dans l'API REST.
 * TODO — business implementation : construire a partir de la liste de Document du domaine.
 */
public record DocumentListResponse(List<DocumentResponse> documents) {
}
