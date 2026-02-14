package com.orcamento.api.extension;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.logging.Logger;

public class MemoryMonitorExtension implements BeforeEachCallback, AfterEachCallback {

    private static final Logger logger = Logger.getLogger(MemoryMonitorExtension.class.getName());
    private static final String MEMORY_BEFORE = "memory_before";
    private static final String TIME_BEFORE = "time_before";

    @Override
    public void beforeEach(ExtensionContext context) {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc(); // Força coleta de lixo antes de medir
        
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        long startTime = System.currentTimeMillis();
        
        ExtensionContext.Store store = getStore(context);
        store.put(MEMORY_BEFORE, usedMemory);
        store.put(TIME_BEFORE, startTime);
    }

    @Override
    public void afterEach(ExtensionContext context) {
        Runtime runtime = Runtime.getRuntime();
        
        ExtensionContext.Store store = getStore(context);
        long memoryBefore = store.get(MEMORY_BEFORE, long.class);
        long timeBefore = store.get(TIME_BEFORE, long.class);
        
        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsed = memoryAfter - memoryBefore;
        long duration = System.currentTimeMillis() - timeBefore;
        
        String testName = context.getDisplayName();
        
        // Log formatado
        logger.info(String.format(
            "\n┌─────────────────────────────────────────────────────────────┐" +
            "\n│ 🧪 Teste: %-50s│" +
            "\n├─────────────────────────────────────────────────────────────┤" +
            "\n│ ⏱️  Tempo:         %-41s│" +
            "\n│ 💾 Memória antes:  %-41s│" +
            "\n│ 💾 Memória depois: %-41s│" +
            "\n│ 📊 Memória usada:  %-41s│" +
            "\n│ 📈 RAM total JVM:  %-41s│" +
            "\n└─────────────────────────────────────────────────────────────┘",
            truncate(testName, 50),
            duration + " ms",
            formatBytes(memoryBefore),
            formatBytes(memoryAfter),
            formatBytes(Math.max(0, memoryUsed)),
            formatBytes(runtime.totalMemory())
        ));
    }

    private ExtensionContext.Store getStore(ExtensionContext context) {
        return context.getStore(ExtensionContext.Namespace.create(getClass(), context.getRequiredTestMethod()));
    }

    private String formatBytes(long bytes) {
        if (bytes < 0) bytes = 0;
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.2f %sB", bytes / Math.pow(1024, exp), pre);
    }
    
    private String truncate(String str, int maxLength) {
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }
}