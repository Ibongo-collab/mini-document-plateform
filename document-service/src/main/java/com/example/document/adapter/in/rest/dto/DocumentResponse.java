package com.example.document.adapter.in.rest.dto;

import java.time.Instant;

/**
 * DTO de sortie representant un document dans l'API REST.
 * Ne jamais exposer directement les objets du domaine dans l'API.
 * TODO — business implementation : mapper depuis com.example.document.domain.model.Document.
 */
public record DocumentResponse(
        String id,
        String filename,
        long size,
        String status,
        Instant createdAt
) {
}
