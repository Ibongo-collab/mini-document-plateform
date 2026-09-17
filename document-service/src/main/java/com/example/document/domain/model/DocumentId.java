package com.example.document.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object identifiant un Document de maniere unique.
 * L'égalité est basee sur la valeur, pas sur la reference (comportement standard d'un Value Object).
 */
public record DocumentId(UUID value) {

    public DocumentId(UUID value) {
        this.value = Objects.requireNonNull(value, "value must not be null");
    }

    public static DocumentId newId() {
        return new DocumentId(UUID.randomUUID());
    }

    public static DocumentId of(UUID value) {
        return new DocumentId(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DocumentId that)) return false;
        return value.equals(that.value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
