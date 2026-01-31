package com.orcamento.api.entity.enums;

public enum BillingMethod {
    WORD, PARAGRAPH, CHARACTER, PAGE;

    public static String siglaToBillingMethod(String valor) {
        if (valor == null) throw new IllegalArgumentException("Valor não pode ser nulo");

        switch (valor.toUpperCase()) {
            case "W":
            case "WORD":
                return "WORD";
            case "P":
            case "PARAGRAPH":
                return "PARAGRAPH";
            case "C":
            case "CHARACTER":
                return "CHARACTER";
            case "PG":
            case "PAGE":
                return "PAGE";
            default:
                throw new IllegalArgumentException("Sigla ou método desconhecido: " + valor);
        }
    }
}