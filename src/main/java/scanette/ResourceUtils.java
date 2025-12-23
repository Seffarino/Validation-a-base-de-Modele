package scanette;

import java.io.*;
import java.nio.file.*;

public class ResourceUtils {

    public static File loadCsvFromResources(String resourcePath) {
        try (InputStream is = ResourceUtils.class
                .getClassLoader()
                .getResourceAsStream(resourcePath)) {

            if (is == null) {
                throw new RuntimeException(
                        "Ressource introuvable dans le classpath : " + resourcePath);
            }

            File tmp = File.createTempFile("produits", ".csv");
            tmp.deleteOnExit();

            Files.copy(is, tmp.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return tmp;

        } catch (IOException e) {
            throw new RuntimeException("Erreur chargement ressource " + resourcePath, e);
        }
    }
}