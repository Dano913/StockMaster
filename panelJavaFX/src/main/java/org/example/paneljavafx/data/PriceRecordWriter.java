package org.example.paneljavafx.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.example.paneljavafx.model.PriceRecord;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class PriceRecordWriter {

    // -------------------------
    // RUTA DEL ARCHIVO — resuelta desde la ubicación del .class
    // -------------------------
    private static final String PATH = resolveDataPath();

    private static String resolveDataPath() {
        try {
            String classesPath = PriceRecordWriter.class
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()
                    .getPath();

            // classesPath = .../target/classes/  →  subimos dos niveles a la raíz
            File projectRoot = new File(classesPath)
                    .getParentFile()  // target
                    .getParentFile(); // raíz del proyecto

            String resolved = projectRoot + "/src/main/resources/data/precio_asset.json";
            System.out.println("📁 PriceRecordWriter → " + resolved);
            return resolved;

        } catch (Exception e) {
            System.err.println("⚠️ No se pudo resolver ruta, usando relativa");
            return "src/main/resources/data/precio_asset.json";
        }
    }

    // -------------------------
    // JACKSON
    // -------------------------
    private static final ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    // -------------------------
    // COLA — los engines depositan aquí, el writer consume solo
    // -------------------------
    private static final BlockingQueue<PriceRecord> queue = new LinkedBlockingQueue<>();

    // -------------------------
    // WRITER THREAD — un solo hilo escribe, arranca con la clase
    // -------------------------
    static {
        Thread writer = new Thread(() -> {
            while (true) {
                try {
                    PriceRecord record = queue.take();
                    writeToFile(record);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "price-record-writer");

        writer.setDaemon(true);
        writer.start();
    }

    // -------------------------
    // API PÚBLICA — no bloquea
    // -------------------------
    public static void append(PriceRecord record) {
        queue.offer(record);
    }

    // -------------------------
    // ESCRITURA REAL — un solo hilo a la vez
    // -------------------------
    private static void writeToFile(PriceRecord record) {

        try {
            File file = new File(PATH);

            // asegurar que el directorio existe
            file.getParentFile().mkdirs();

            String content = file.exists()
                    ? new String(java.nio.file.Files.readAllBytes(file.toPath())).trim()
                    : "";

            if (content.isEmpty() || content.equals("[]")) {
                mapper.writeValue(file, new PriceRecord[]{record});
                return;
            }

            // append eficiente — busca el último ']' y añade antes de él
            try (var raf = new java.io.RandomAccessFile(file, "rw")) {

                long pos = file.length() - 1;

                while (pos >= 0) {
                    raf.seek(pos);
                    int b = raf.read();
                    if (b == ']') {
                        raf.setLength(pos);
                        raf.seek(pos);
                        String entry = ",\n  " + mapper.writeValueAsString(record) + "\n]";
                        raf.write(entry.getBytes());
                        return;
                    }
                    pos--;
                }

                // no encontró ']' → corrupto, reiniciar
                System.err.println("⚠️ precio_asset.json corrupto — reiniciando");
                mapper.writeValue(file, new PriceRecord[]{record});
            }

        } catch (IOException e) {
            System.err.println("❌ Error escribiendo PriceRecord: " + e.getMessage());
        }
    }
}