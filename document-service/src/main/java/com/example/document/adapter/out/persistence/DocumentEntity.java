package com.example.document.adapter.out.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * Entite JPA de persistance, distincte de l'Aggregate Root du domaine.
 * <p>
 * NE PAS ecrire la persistance ici : structure preparee uniquement.
 * TODO — business implementation : ajouter les annotations de colonnes,
 * contraintes, index, etc. une fois PostgreSQL branche.
 */
@Entity
@Table(name = "documents")
public class DocumentEntity {

    @Id
    private UUID id;

    private String filename;

    private long size;

    private String storageKey;

    private String status;

    private Instant createdAt;

    protected DocumentEntity() {
        // requis par JPA
    }

    // TODO — business implementation : constructeur(s), getters/setters
    // ou approche immuable selon le choix de mapping retenu.
}
