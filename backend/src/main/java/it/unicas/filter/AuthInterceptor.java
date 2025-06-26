package it.unicas.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import it.unicas.util.JwtUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

public class AuthInterceptor extends AbstractInterceptor {

    private static final Logger logger = LogManager.getLogger(AuthInterceptor.class);

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();

        String authHeader = request.getHeader("Authorization");

        /** Non viene mai invocato
        // Consenti alle richieste OPTIONS di passare (preflight CORS)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return invocation.invoke();
        }
        **/


        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Tentativo di accesso non autorizzato: Header Authorization mancante o malformato.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
            response.getWriter().write("{\"message\": \"Non autorizzato: Token mancante o non valido.\"}");
            response.setContentType("application/json");
            return null; // Ferma ulteriore elaborazione
        }

        String token = authHeader.substring(7); // Rimuovi il prefisso "Bearer "

        try {
            JwtUtil.validateToken(token);
            logger.debug("Token JWT validato con successo per la richiesta a {}", request.getRequestURI());
            // Token valido, prosegui con l'azione
            return invocation.invoke();
        } catch (JWTVerificationException e) {
            logger.warn("Tentativo di accesso non autorizzato: Token JWT non valido. Dettagli: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
            response.getWriter().write("{\"message\": \"Non autorizzato: Token non valido o scaduto.\"}");
            response.setContentType("application/json");
            return null; // Ferma ulteriore elaborazione
        } catch (IllegalStateException e) {
            // Cattura il caso in cui JWT_SECRET_KEY non sia impostata nelle variabili d'ambiente
            logger.fatal("Errore di configurazione del server: JWT Secret Key non impostata. Dettagli: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500 Internal Server Error
            response.getWriter().write("{\"message\": \"Errore di configurazione del server.\"}");
            response.setContentType("application/json");
            return null;
        }
    }
}