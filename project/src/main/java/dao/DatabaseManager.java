package dao;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.*;

public class DatabaseManager {

    private final Connection conn;

    private final int schemaVersion = 1;


    private int getSchemaVersion(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA user_version")) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private void setSchemaVersion(Connection conn, int version) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA user_version = " + version);
        }
    }

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

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Błąd inicjalizacji bazy danych", e);
        }
    }

    private boolean isFreshDatabase() throws SQLException {
        String sql = "SELECT COUNT(*) FROM sqlite_master WHERE type = 'table' AND name NOT LIKE 'sqlite_%'";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() && rs.getInt(1) == 0;
        }
    }

    private void setupOrMigrate() throws SQLException, IOException {
        if (isFreshDatabase()){
            runScript("schema" + schemaVersion + ".sql") ;
        }
        else {
            int currentSchemaVersion = getSchemaVersion(conn);
            while (currentSchemaVersion != schemaVersion) {

            }
        }

    }

    private void applyMigration(int goalVersion){

    }

    private void runScript(String resourcePath) throws SQLException, IOException {
        String sql;

        try (InputStream is = getClass().getClassLoader()
                .getResourceAsStream(resourcePath)) {

            if (is == null) {
                throw new RuntimeException("Resource not found: " + resourcePath);
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
    }

    public Connection getConnection() {
        return conn;
    }
}
