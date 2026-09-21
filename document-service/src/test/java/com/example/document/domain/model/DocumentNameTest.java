package com.example.document.domain.model;

import com.example.document.domain.exception.DocumentValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("DocumentName (Value Object)")
class DocumentNameTest {

    @Test
    @DisplayName("of() cree un DocumentName valide")
    void ofCreatesValidDocumentName() {
        DocumentName name = DocumentName.of("rapport.pdf");

        assertThat(name.value()).isEqualTo("rapport.pdf");
    }

    @Test
    @DisplayName("rejette une valeur null")
    void rejectsNullValue() {
        assertThatThrownBy(() -> DocumentName.of(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("rejette une valeur vide ou blanche")
    void rejectsBlankValue() {
        assertThatThrownBy(() -> DocumentName.of("   "))
                .isInstanceOf(DocumentValidationException.class)
                .hasMessageContaining("blank");
    }

    @Test
    @DisplayName("rejette un nom de plus de 255 caracteres")
    void rejectsTooLongValue() {
        String tooLong = "a".repeat(256);

        assertThatThrownBy(() -> DocumentName.of(tooLong))
                .isInstanceOf(DocumentValidationException.class)
                .hasMessageContaining("255");
    }

    @Test
    @DisplayName("accepte exactement 255 caracteres")
    void acceptsExactly255Characters() {
        String exactly255 = "a".repeat(255);

        assertThat(DocumentName.of(exactly255).value()).hasSize(255);
    }

    @Test
    @DisplayName("deux DocumentName avec la meme valeur sont egaux")
    void equalityIsBasedOnValue() {
        assertThat(DocumentName.of("a.pdf")).isEqualTo(DocumentName.of("a.pdf"));
    }

    @Test
    @DisplayName("toString() renvoie la valeur brute")
    void toStringReturnsRawValue() {
        assertThat(DocumentName.of("a.pdf")).hasToString("a.pdf");
    }
}
