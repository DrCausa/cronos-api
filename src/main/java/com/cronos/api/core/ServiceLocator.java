package com.cronos.api.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Contenedor ligero de Inyección de Dependencias (DI).
 * Almacena las instancias únicas (Singletons) de nuestros servicios y configuraciones.
 */
public class ServiceLocator {

    // Utilizamos ConcurrentHashMap para garantizar la seguridad en entornos multihilo (Thread-safe)
    private final Map<Class<?>, Object> services = new ConcurrentHashMap<>();

    /**
     * Registra una instancia en el contenedor.
     * * @param clazz La clase o interfaz que servirá como identificador.
     * @param service La instancia real de la dependencia.
     */
    public <T> void register(Class<T> clazz, T service) {
        services.put(clazz, service);
    }

    /**
     * Recupera una dependencia previamente registrada.
     * * @param clazz La clase o interfaz de la dependencia a buscar.
     * @return La instancia solicitada.
     * @throws IllegalArgumentException si la dependencia no fue registrada en el Bootstrap.
     */
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> clazz) {
        Object service = services.get(clazz);
        if (service == null) {
            throw new IllegalArgumentException("CRÍTICO: Dependencia no encontrada para la clase " + clazz.getName() + 
                                               ". ¿Olvidaste registrarla en el Bootstrap?");
        }
        return (T) service;
    }
}
