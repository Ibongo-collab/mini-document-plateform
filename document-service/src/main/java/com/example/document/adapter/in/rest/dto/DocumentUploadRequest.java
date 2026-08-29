package com.example.document.adapter.in.rest.dto;

import org.springframework.web.multipart.MultipartFile;

/**
 * DTO d'entree pour l'upload d'un document via l'API REST.
 * <p>
 * Ne jamais exposer directement les objets du domaine dans l'API.
 * TODO — business implementation : ajouter la validation (annotations
 * Bean Validation ou verifications manuelles) selon les besoins.
 */
public record DocumentUploadRequest(MultipartFile file) {
}
