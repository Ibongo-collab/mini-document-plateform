package com.example.document.infrastructure.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repository Spring Data JPA technique.
 */
public interface DocumentJpaRepository extends JpaRepository<DocumentEntity, UUID> {
}
