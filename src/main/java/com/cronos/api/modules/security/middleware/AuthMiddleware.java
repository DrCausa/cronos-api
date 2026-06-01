package com.cronos.api.modules.security.middleware;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.UnauthorizedResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Interceptor que valida el JWT en las rutas protegidas.
 */
public class AuthMiddleware implements Handler {

    private static final Logger log = LoggerFactory.getLogger(AuthMiddleware.class);
    private final JWTVerifier verifier;

    public AuthMiddleware(String jwtSecret) {
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
        this.verifier = JWT.require(algorithm)
                .withIssuer("cronos-api")
                .build();
    }

    @Override
    public void handle(Context ctx) throws Exception {
        
        if (ctx.method().name().equals("OPTIONS")) {
            return;
        }
        
        String header = ctx.header("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            log.warn("Intento de acceso denegado (Sin token) en ruta: {}", ctx.path());
            // Javalin 5 detiene el flujo lanzando esta excepción y devuelve un JSON 401 automáticamente
            throw new UnauthorizedResponse("Se requiere un token de autenticación (Bearer).");
        }

        String token = header.substring(7);

        try {
            DecodedJWT jwt = verifier.verify(token);
            Integer userId = Integer.parseInt(jwt.getSubject());
            
            // Inyectamos el ID en el contexto para el resto de la petición
            ctx.attribute("userId", userId);
            
        } catch (JWTVerificationException e) {
            log.warn("Intento de acceso denegado (Token inválido/expirado) en ruta: {}", ctx.path());
            // Detiene el flujo por token corrupto o expirado
            throw new UnauthorizedResponse("El token es inválido o ha expirado. Inicia sesión nuevamente.");
        }
    }
}
