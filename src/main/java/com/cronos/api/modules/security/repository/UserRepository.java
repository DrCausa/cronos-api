package com.cronos.api.modules.security.repository;

import com.cronos.api.core.DatabaseManager;
import com.cronos.api.modules.security.model.User;
import com.cronos.api.modules.security.model.UserStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;

public class UserRepository {

    private static final Logger log = LoggerFactory.getLogger(UserRepository.class);
    private final DatabaseManager dbManager;

    // Inyección de dependencia: El repositorio necesita el manager para obtener conexiones
    public UserRepository(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    /**
     * Guarda un nuevo usuario en la base de datos.
     * @param user Objeto User con los datos a guardar (sin ID).
     * @return El mismo objeto User, pero con el ID autogenerado por MySQL asignado.
     */
    public User save(User user) {
        String sql = "INSERT INTO `user` (username, pwd_hash, email, status) VALUES (?, ?, ?, ?)";

        // El try-with-resources garantiza que la conexión y el statement se cierren automáticamente
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPwdHash());
            stmt.setString(3, user.getEmail());
            // Guardamos el Enum como String ('ACTIVE', 'INACTIVE', etc.)
            stmt.setString(4, user.getStatus().name()); 

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Fallo al crear el usuario, no se insertaron filas.");
            }

            // Recuperamos el ID autoincremental que MySQL le asignó
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                    //user.setCreatedAt(LocalDateTime.now());
                } else {
                    throw new SQLException("Fallo al crear el usuario, no se obtuvo el ID.");
                }
            }
            return user;

        } catch (SQLException e) {
            log.error("Error al guardar el usuario en la BD: {}", user.getUsername(), e);
            throw new RuntimeException("Error interno de base de datos al guardar usuario.", e);
        }
    }

    /**
     * Busca un usuario por su nombre de usuario.
     * @param username El nombre de usuario a buscar.
     * @return Un Optional que contiene el User si existe, o vacío si no se encuentra.
     */
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM `user` WHERE username = ?";
        return executeQueryAndMapToUser(sql, username);
    }

    /**
     * Busca un usuario por su correo electrónico.
     * @param email El correo a buscar.
     * @return Un Optional que contiene el User si existe, o vacío si no se encuentra.
     */
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM `user` WHERE email = ?";
        return executeQueryAndMapToUser(sql, email);
    }

    /**
     * Método auxiliar privado para evitar código repetido al hacer SELECTs simples.
     */
    private Optional<User> executeQueryAndMapToUser(String sql, String parameter) {
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, parameter);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            log.error("Error al ejecutar consulta de usuario con parámetro: {}", parameter, e);
            throw new RuntimeException("Error de base de datos al buscar usuario.", e);
        }
        return Optional.empty(); // Retorna vacío si no hay resultados
    }

    /**
     * Mapea una fila del ResultSet de MySQL a nuestro objeto Java User.
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPwdHash(rs.getString("pwd_hash"));
        user.setEmail(rs.getString("email"));
        user.setStatus(UserStatus.valueOf(rs.getString("status")));
        
        // Manejo de fechas convirtiendo Timestamp a LocalDateTime
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        user.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        
        return user;
    }
}
