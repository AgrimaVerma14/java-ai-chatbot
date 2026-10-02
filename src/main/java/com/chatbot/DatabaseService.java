package com.chatbot;

import java.sql.*;
import java.time.LocalDateTime;

public class DatabaseService {

    private static final String URL =
            System.getenv().getOrDefault(
                    "CHATBOT_DB_URL",
                    "jdbc:mysql://localhost:3306/java_chatbot?useSSL=false&serverTimezone=UTC"
            );

    private static final String USER =
            System.getenv().getOrDefault(
                    "CHATBOT_DB_USER",
                    "root"
            );

    private static final String PASSWORD =
            System.getenv().getOrDefault(
                    "CHATBOT_DB_PASSWORD",
                    ""
            );

    public DatabaseService() {
        initializeDatabase();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private void initializeDatabase() {

        String databaseName = "java_chatbot";

        String serverUrl =
                "jdbc:mysql://localhost:3306/?useSSL=false&serverTimezone=UTC";

        try (Connection connection =
                     DriverManager.getConnection(serverUrl, USER, PASSWORD);
             Statement statement = connection.createStatement()) {

            statement.executeUpdate(
                    "CREATE DATABASE IF NOT EXISTS " + databaseName
            );

        } catch (SQLException e) {

            System.out.println(
                    "Could not create database automatically: "
                            + e.getMessage()
            );
        }

        String usersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    firebase_uid VARCHAR(128) NOT NULL UNIQUE,
                    email VARCHAR(255) NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """;

        String chatsTable = """
                CREATE TABLE IF NOT EXISTS chats (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    firebase_uid VARCHAR(128) NOT NULL,
                    role VARCHAR(20) NOT NULL,
                    message TEXT NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    INDEX idx_user_time (firebase_uid, created_at)
                )
                """;

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            statement.executeUpdate(usersTable);
            statement.executeUpdate(chatsTable);

            System.out.println("JDBC database initialized successfully.");

        } catch (SQLException e) {

            System.out.println(
                    "JDBC initialization error: "
                            + e.getMessage()
            );
        }
    }

    public void saveUser(String firebaseUid, String email) {

        if (firebaseUid == null || email == null) {
            return;
        }

        String sql = """
                INSERT INTO users (firebase_uid, email)
                VALUES (?, ?)
                ON DUPLICATE KEY UPDATE email = VALUES(email)
                """;

        try (Connection connection = getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, firebaseUid);
            statement.setString(2, email);

            statement.executeUpdate();

        } catch (SQLException e) {

            System.out.println(
                    "Could not save user: "
                            + e.getMessage()
            );
        }
    }

    public void saveMessage(
            String firebaseUid,
            String role,
            String message
    ) {

        if (firebaseUid == null ||
                role == null ||
                message == null ||
                message.isBlank()) {

            return;
        }

        String sql = """
                INSERT INTO chats
                (firebase_uid, role, message)
                VALUES (?, ?, ?)
                """;

        try (Connection connection = getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, firebaseUid);
            statement.setString(2, role);
            statement.setString(3, message);

            statement.executeUpdate();

        } catch (SQLException e) {

            System.out.println(
                    "Could not save chat message: "
                            + e.getMessage()
            );
        }
    }

    public String loadRecentMessages(
            String firebaseUid,
            int limit
    ) {

        if (firebaseUid == null) {
            return "";
        }

        String sql = """
                SELECT role, message, created_at
                FROM chats
                WHERE firebase_uid = ?
                ORDER BY created_at DESC
                LIMIT ?
                """;

        StringBuilder result = new StringBuilder();

        try (Connection connection = getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, firebaseUid);
            statement.setInt(2, limit);

            try (ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {

                    String role = rs.getString("role");
                    String message = rs.getString("message");
                    Timestamp timestamp =
                            rs.getTimestamp("created_at");

                    result.insert(
                            0,
                            "[" + timestamp + "] "
                                    + role
                                    + ": "
                                    + message
                                    + "\n\n"
                    );
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Could not load chat history: "
                            + e.getMessage()
            );
        }

        return result.toString();
    }

    public void clearUserChats(String firebaseUid) {

        if (firebaseUid == null) {
            return;
        }

        String sql =
                "DELETE FROM chats WHERE firebase_uid = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, firebaseUid);
            statement.executeUpdate();

        } catch (SQLException e) {

            System.out.println(
                    "Could not clear chat history: "
                            + e.getMessage()
            );
        }
    }

    public void close() {
        // Connections are opened and closed per operation.
        // No persistent connection is required.
    }
}