package com.example.document.infrastructure.in.dto;

import java.time.Instant;

/**
 * DTO de sortie representant un document dans l'API REST.
 * Ne jamais exposer directement les objets du domaine dans l'API.
 */
public record DocumentResponse(
        String id,
        String filename,
        long size,
        String status,
        Instant createdAt
) {
}
