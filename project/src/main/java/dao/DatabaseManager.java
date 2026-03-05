package dao;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseManager {

    private final Connection conn;

    public DatabaseManager() {
        try {
            Path appDir = Paths.get(System.getProperty("user.home"), ".mytodoapp");
            Files.createDirectories(appDir);

            Path dbFile = appDir.resolve("database.db");
            String url = "jdbc:sqlite:" + dbFile.toAbsolutePath();


            conn = DriverManager.getConnection(url);


            try (Statement pragma = conn.createStatement()) {
                pragma.execute("PRAGMA foreign_keys = ON;");
            }


            String sql;

            try (InputStream is = getClass().getClassLoader()
                    .getResourceAsStream("schema.sql")) {

                if (is == null) {
                    throw new RuntimeException("Nie znaleziono schema.sql w resources");
                }

                sql = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }

            try (Statement stmt = conn.createStatement()) {
                String[] statements = sql.split(";");
                for (String statement : statements) {
                    if (!statement.trim().isEmpty()) {
                        stmt.execute(statement);
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Błąd inicjalizacji bazy danych", e);
        }
    }

    public Connection getConnection() {
        return conn;
    }
}
