package com.example.document.domain.model;

import java.util.Objects;

/**
 * Value Object representant la taille d'un document, en octets.
 * <p>
 * TODO — business implementation : definir les invariants (taille minimale,
 * taille maximale autorisee, etc.) selon les regles metier souhaitees.
 */
public final class DocumentSize {

    private final long bytes;

    private DocumentSize(long bytes) {
        // TODO — business implementation : valider que bytes >= 0,
        // et eventuellement definir une taille maximale.
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
