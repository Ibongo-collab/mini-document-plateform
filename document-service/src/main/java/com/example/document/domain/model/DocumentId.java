package com.example.document.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object identifiant un Document de maniere unique.
 * <p>
 * Java pur — aucune dependance framework. L'egalite est basee sur la valeur,
 * pas sur la reference (comportement standard d'un Value Object).
 */
public final class DocumentId {

    private final UUID value;

    private DocumentId(UUID value) {
        this.value = Objects.requireNonNull(value, "value must not be null");
    }

    public static DocumentId newId() {
        return new DocumentId(UUID.randomUUID());
    }

    public static DocumentId of(UUID value) {
        return new DocumentId(value);
    }

    public static DocumentId of(String value) {
        return new DocumentId(UUID.fromString(value));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DocumentId that)) return false;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
