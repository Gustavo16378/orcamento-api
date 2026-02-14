package com.orcamento.api.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TemplateUtils {

    // Lê o arquivo do classpath (src/main/resources)
    public static String loadTemplateFromClasspath(String resourcePath) throws IOException {
        try (InputStream is = TemplateUtils.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IOException("Template não encontrado: " + resourcePath);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    public static String processTemplate(String template, String nomeCliente, String tipoOrcamento, String idOrcamento) {
        return template
                .replace("{{NOME_CLIENTE}}", nomeCliente)
                .replace("{{TIPO_ORCAMENTO}}", tipoOrcamento)
                .replace("{{ID_ORCAMENTO}}", idOrcamento);
    }
}