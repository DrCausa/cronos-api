package com.cronos.api.core;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {
    
    private static final Logger log = LoggerFactory.getLogger(DatabaseManager.class);
    private final HikariDataSource dataSource;

    public DatabaseManager(Dotenv dotenv) {
        log.info("Inicializando conexión a la base de datos MySQL...");
        
        HikariConfig config = new HikariConfig();
        
        // Construcción de la URL de conexión leyendo desde el archivo .env
        String host = dotenv.get("DB_HOST", "localhost");
        String port = dotenv.get("DB_PORT", "3306");
        String dbName = dotenv.get("DB_NAME", "cronos_db_v1");
        
        // Se añaden parámetros clave para MySQL 8 (Timezone y PublicKeyRetrieval)
        String jdbcUrl = String.format("jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true", host, port, dbName);
        
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(dotenv.get("DB_USER"));
        config.setPassword(dotenv.get("DB_PASSWORD"));
        
        // ==========================================================
        // Optimizaciones críticas de rendimiento para MySQL + HikariCP
        // ==========================================================
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");

        // Límites del Pool (Preparado para concurrencia)
        config.setMaximumPoolSize(10); 
        config.setMinimumIdle(2);
        config.setIdleTimeout(30000);
        config.setConnectionTimeout(10000);
        config.setMaxLifetime(1800000); // 30 minutos

        this.dataSource = new HikariDataSource(config);
        log.info("Pool de conexiones HikariCP configurado y activo.");
    }

    /**
     * Devuelve una conexión activa del pool.
     * Importante: Quien la solicite DEBE cerrarla en un bloque try-with-resources.
     */
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Cierra el pool por completo de forma elegante (útil al apagar el servidor).
     */
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            log.info("Cerrando el pool de conexiones...");
            dataSource.close();
        }
    }
}
