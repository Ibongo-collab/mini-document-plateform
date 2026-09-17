package com.example.document.domain.model;

import com.example.document.domain.exception.DocumentValidationException;

import java.util.Objects;

/**
 * Value Object representant la taille d'un document, en octets.
 * <p>
 * TODO — business implementation : definir les invariants (taille minimale,
 * taille maximale autorisee, etc.) selon les regles metier souhaitees.
 */
public final class DocumentSize {

    private final long bytes;
    private static final long MAX_ALLOWED_BYTES = 5 * 1024 * 1024;

    private DocumentSize(long bytes) {
        if (bytes < 0) {
            throw new IllegalArgumentException("bytes must be > 0");
        }
        if (bytes > MAX_ALLOWED_BYTES) {
            throw new DocumentValidationException("Document size exceeds the maximum allowed limit of " + MAX_ALLOWED_BYTES);
        }
        this.bytes = bytes;
    }

    public static DocumentSize ofBytes(long bytes) {
        return new DocumentSize(bytes);
    }

    public long bytes() {
        return bytes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DocumentSize that)) return false;
        return bytes == that.bytes;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bytes);
    }

    @Override
    public String toString() {
        return bytes + " bytes";
    }
}
