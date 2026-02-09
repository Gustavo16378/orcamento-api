package com.orcamento.api.entity.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Testes do BillingMethod Enum")
class BillingMethodTest {

    @Test
    @DisplayName("Deve converter sigla 'W' para 'WORD'")
    void deveConverterWSigla() {
        String result = BillingMethod.siglaToBillingMethod("W");
        assertThat(result).isEqualTo("WORD");
    }

    @Test
    @DisplayName("Deve converter 'WORD' para 'WORD'")
    void deveConverterWORD() {
        String result = BillingMethod.siglaToBillingMethod("WORD");
        assertThat(result).isEqualTo("WORD");
    }

    @Test
    @DisplayName("Deve converter 'P' para 'PARAGRAPH'")
    void deveConverterPSigla() {
        String result = BillingMethod.siglaToBillingMethod("P");
        assertThat(result).isEqualTo("PARAGRAPH");
    }

    @Test
    @DisplayName("Deve converter 'C' para 'CHARACTER'")
    void deveConverterCSigla() {
        String result = BillingMethod.siglaToBillingMethod("C");
        assertThat(result).isEqualTo("CHARACTER");
    }

    @Test
    @DisplayName("Deve converter 'PG' para 'PAGE'")
    void deveConverterPGSigla() {
        String result = BillingMethod.siglaToBillingMethod("PG");
        assertThat(result).isEqualTo("PAGE");
    }

    @Test
    @DisplayName("Deve ignorar case (lowercase/uppercase)")
    void deveIgnorarCase() {
        assertThat(BillingMethod.siglaToBillingMethod("w")).isEqualTo("WORD");
        assertThat(BillingMethod.siglaToBillingMethod("Word")).isEqualTo("WORD");
        assertThat(BillingMethod.siglaToBillingMethod("pg")).isEqualTo("PAGE");
    }

    @Test
    @DisplayName("Deve lançar exceção para valor nulo")
    void deveLancarExcecaoParaNull() {
        assertThatThrownBy(() -> BillingMethod.siglaToBillingMethod(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Valor não pode ser nulo");
    }

    @Test
    @DisplayName("Deve lançar exceção para sigla desconhecida")
    void deveLancarExcecaoParaSiglaDesconhecida() {
        assertThatThrownBy(() -> BillingMethod.siglaToBillingMethod("XYZ"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Sigla ou método desconhecido");
    }
}