package com.example.document.domain.model;

import com.example.document.domain.exception.DocumentValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("DocumentSize (Value Object)")
class DocumentSizeTest {

    private static final long MAX_ALLOWED_BYTES = 5 * 1024 * 1024;

    @Test
    @DisplayName("ofBytes() cree une taille valide")
    void ofBytesCreatesValidSize() {
        DocumentSize size = DocumentSize.ofBytes(1024);

        assertThat(size.bytes()).isEqualTo(1024);
    }

    @Test
    @DisplayName("rejette une taille negative")
    void rejectsNegativeSize() {
        assertThatThrownBy(() -> DocumentSize.ofBytes(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("rejette une taille strictement superieure a la limite autorisee")
    void rejectsSizeAboveLimit() {
        assertThatThrownBy(() -> DocumentSize.ofBytes(MAX_ALLOWED_BYTES + 1))
                .isInstanceOf(DocumentValidationException.class);
    }

    @Test
    @DisplayName("accepte exactement la limite autorisee")
    void acceptsExactlyTheLimit() {
        assertThat(DocumentSize.ofBytes(MAX_ALLOWED_BYTES).bytes()).isEqualTo(MAX_ALLOWED_BYTES);
    }

    @Test
    @DisplayName("accepte une taille de zero octet")
    void acceptsZeroBytes() {
        assertThat(DocumentSize.ofBytes(0).bytes()).isZero();
    }

    @Test
    @DisplayName("deux tailles avec la meme valeur sont egales")
    void equalityIsBasedOnValue() {
        assertThat(DocumentSize.ofBytes(100)).isEqualTo(DocumentSize.ofBytes(100));
    }
}
