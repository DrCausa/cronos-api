package com.cronos.api.modules.security.controller;

import com.cronos.api.modules.security.model.TokenResponse;
import com.cronos.api.modules.security.model.UserCreateRequest;
import com.cronos.api.modules.security.model.UserLoginRequest;
import com.cronos.api.modules.security.model.UserResponse;
import com.cronos.api.modules.security.service.UserService;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    // Inyección de dependencia
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * POST /api/users/register
     */
    public void register(Context ctx) {
        log.info("Recibiendo petición de registro de usuario...");
        
        try {
            // 1. Extraer y mapear el JSON del body de la petición
            UserCreateRequest request = ctx.bodyAsClass(UserCreateRequest.class);

            // 2. Llamar a la lógica de negocio
            UserResponse response = userService.registerUser(request);

            // 3. Responder con código 201 (Created) y el usuario creado
            ctx.status(201).json(response);
            log.info("Usuario registrado exitosamente: {}", response.getUsername());

        } catch (IllegalArgumentException e) {
            // Error de negocio (ej. usuario duplicado, contraseña vacía) -> 400 Bad Request
            log.warn("Error de validación al registrar usuario: {}", e.getMessage());
            ctx.status(400).json(java.util.Map.of(
                "error", "Bad Request",
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            // Error inesperado (ej. JSON mal formado) -> 500 Internal Server Error
            log.error("Error inesperado en el registro de usuario", e);
            ctx.status(500).json(java.util.Map.of(
                "error", "Internal Server Error",
                "message", "Ocurrió un error al procesar el registro."
            ));
        }
    }
    
    /**
     * POST /api/users/login
     */
    public void login(Context ctx) {
        log.info("Recibiendo petición de inicio de sesión...");
        
        try {
            UserLoginRequest request = ctx.bodyAsClass(UserLoginRequest.class);
            TokenResponse response = userService.login(request);
            
            // Retornamos 200 OK y el JSON con el JWT
            ctx.status(200).json(response);
            log.info("Usuario autenticado exitosamente: {}", request.getUsername());

        } catch (IllegalArgumentException e) {
            // Devolvemos 401 Unauthorized si la contraseña o el usuario fallan
            log.warn("Intento de login fallido: {}", e.getMessage());
            ctx.status(401).json(java.util.Map.of(
                "error", "Unauthorized",
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("Error inesperado en el inicio de sesión", e);
            ctx.status(500).json(java.util.Map.of(
                "error", "Internal Server Error",
                "message", "Error al procesar el login."
            ));
        }
    }
}
