package com.orcamento.api.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TemplateUtils {

    public static String loadTemplateFromClasspath(String resourcePath) throws IOException {
        try (InputStream is = TemplateUtils.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IOException("Template não encontrado: " + resourcePath);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    public static String processTemplate(String template, String nome, String tipo, String id) {
    return template
        .replace("{{nome}}", nome)
        .replace("{{budget_type}}", tipo)
        .replace("{{orcamento_id}}", id);
}
}