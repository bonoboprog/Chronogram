package it.unicas.config;

import it.unicas.util.FirebaseInitializer;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Listener che si attiva all'avvio dell'applicazione web.
 * Inizializza i servizi necessari, come Firebase.
 */

public class AppStartupListener implements ServletContextListener {

    private static final Logger logger = LogManager.getLogger(AppStartupListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("📦 Avvio applicazione: inizializzazione servizi globali...");
        FirebaseInitializer.initFirebase();
        logger.info("✅ Inizializzazione completata.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("🧹 Arresto applicazione: cleanup finale...");
        // (Opzionale: chiudi risorse se necessario)
    }
}