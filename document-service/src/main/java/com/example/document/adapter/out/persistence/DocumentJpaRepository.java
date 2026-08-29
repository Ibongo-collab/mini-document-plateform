package com.example.document.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repository Spring Data JPA technique.
 * NE PAS implementer : Spring Data genere l'implementation.
 * TODO — business implementation : ajouter des methodes de requete
 * personnalisees si necessaire.
 */
public interface DocumentJpaRepository extends JpaRepository<DocumentEntity, UUID> {
}
