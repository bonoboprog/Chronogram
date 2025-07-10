package it.unicas.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import it.unicas.util.JwtUtil;
import it.unicas.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * Interceptor per l'autenticazione JWT che verifica la presenza e validità
 * del token nelle richieste HTTP.
 *
 * Estende AbstractInterceptor di Struts2 per integrare la logica di autenticazione
 * nel flusso delle azioni.
 */
public class AuthInterceptor extends AbstractInterceptor {

    // Logger per tracciare eventi e errori
    private static final Logger logger = LogManager.getLogger(AuthInterceptor.class);

    /**
     * Metodo principale dell'interceptor che viene eseguito per ogni richiesta.
     *
     * @param invocation L'oggetto che rappresenta l'invocazione dell'azione Struts2
     * @return Il risultato dell'azione se l'autenticazione ha successo, altrimenti null
     * @throws Exception Eccezioni generali durante l'elaborazione
     */
    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return null;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Tentativo di accesso non autorizzato: Header Authorization mancante o malformato.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Non autorizzato: Token mancante\"}");
            return null;
        }

        String token = authHeader.substring(7);

        try {
            // ✅ 1. Valida il token e ottieni l'oggetto DecodedJWT
            DecodedJWT decodedJWT = JwtUtil.validateToken(token);

            // ✅ 2. Estrai l'email (supponendo sia nel claim "sub" o "email")
            String email = decodedJWT.getSubject();  // oppure decodedJWT.getClaim("email").asString()

            // ✅ 3. Recupera l'ID utente dal DB (tramite service/DAO)
            // Esempio: UserService.getUserIdByEmail(email);
            Integer userId = UserService.getInstance().getUserIdByEmail(email);
            if (userId == null) {
                logger.warn("Email non trovata nel sistema: {}", email);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"message\": \"Utente non autorizzato.\"}");
                return null;
            }

            // ✅ 4. Inserisci l’ID utente nella request (o ActionContext)
            request.setAttribute("authenticatedUserId", userId);
            // oppure: ActionContext.getContext().put("authenticatedUserId", userId);

            logger.debug("Token JWT valido. Utente ID {} autenticato con email {}", userId, email);

            // ✅ 5. Prosegui con l’azione
            return invocation.invoke();

        } catch (JWTVerificationException e) {
            logger.warn("Token JWT non valido: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Non autorizzato: Token non valido o scaduto.\"}");
            return null;

        } catch (IllegalStateException e) {
            logger.fatal("JWT_SECRET_KEY non impostata: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Errore di configurazione del server.\"}");
            return null;
        }
    }

}