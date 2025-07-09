package it.unicas.util;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;

public class FirebaseInitializer {

    private static final Logger logger = LogManager.getLogger(FirebaseInitializer.class);

    public static void initFirebase() {
        String path = System.getenv("FIREBASE_CREDENTIAL_PATH");

        if (path == null || path.isBlank()) {
            logger.error("❌ Variabile FIREBASE_CREDENTIAL_PATH non trovata.");
            throw new RuntimeException("Variabile d'ambiente FIREBASE_CREDENTIAL_PATH mancante");
        }

        try (FileInputStream serviceAccount = new FileInputStream(path)) {

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);
                logger.info("✅ Firebase inizializzato con successo.");
            } else {
                logger.warn("ℹ Firebase era già stato inizializzato, skip.");
            }

        } catch (IOException e) {
            logger.error("❌ Errore durante l'inizializzazione di Firebase:", e);
            throw new RuntimeException("Errore Firebase", e);
        }
    }
}