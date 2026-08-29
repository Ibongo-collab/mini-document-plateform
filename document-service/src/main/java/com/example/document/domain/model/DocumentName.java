package com.example.document.domain.model;

import java.util.Objects;

/**
 * Value Object representant le nom d'un fichier document (filename).
 * <p>
 * TODO — business implementation : les invariants (longueur maximale,
 * caracteres autorises, extension, etc.) doivent etre ecrits par l'auteur
 * du projet dans le constructeur ou une methode de validation dediee.
 */
public final class DocumentName {

    private final String value;

    private DocumentName(String value) {
        this.value = Objects.requireNonNull(value, "value must not be null");
        // TODO — business implementation : ajouter les regles de validation
        // (non vide, longueur max, caracteres interdits, etc.)
    }

    public static DocumentName of(String value) {
        return new DocumentName(value);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DocumentName that)) return false;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
