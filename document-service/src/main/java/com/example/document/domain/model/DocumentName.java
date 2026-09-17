package com.example.document.domain.model;

import com.example.document.domain.exception.DocumentValidationException;

import java.util.Objects;

/**
 * Value Object representant le nom d'un fichier document (filename).
 */
public record DocumentName(String value) {

    public DocumentName(String value) {
        this.value = Objects.requireNonNull(value, "value must not be null");
        if (value.isBlank()) {
            throw new DocumentValidationException("File name must not be blank");
        } else if (value.length() > 255) {
            throw new DocumentValidationException("File name must not be longer than 255 characters");
        }
    }

    public static DocumentName of(String value) {
        return new DocumentName(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DocumentName that)) return false;
        return value.equals(that.value);
    }

    @Override
    public String toString() {
        return value;
    }
}
