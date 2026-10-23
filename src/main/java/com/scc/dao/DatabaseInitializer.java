package com.scc.dao;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseInitializer {

    public static void initialize() {

        // 1. users (SQL TABLE)
        String usersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    roles TEXT NOT NULL,
                    status TEXT NOT NULL
                )
                """;

        // 2. categories (SQL TABLE)
        String categoriesTable = """
                CREATE TABLE IF NOT EXISTS categories (
                    category_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    category_name TEXT NOT NULL UNIQUE
                )
                """;

        // 3. items (SQL TABLE)
        String itemsTable = """
                CREATE TABLE IF NOT EXISTS items (
                    item_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    item_name TEXT NOT NULL,
                    description TEXT,
                    category_id INTEGER NOT NULL,
                    location TEXT,
                    date_reported TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    status TEXT NOT NULL,
                    reported_by INTEGER NOT NULL,
                    FOREIGN KEY (category_id) REFERENCES categories(category_id),
                    FOREIGN KEY (reported_by) REFERENCES users(id)
                )
                """;

        // 4. claims (SQL TABLE)
        String claimsTable = """
                CREATE TABLE IF NOT EXISTS claims (
                    claim_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    item_id INTEGER NOT NULL,
                    claimant_id INTEGER NOT NULL,
                    id_presented TEXT NOT NULL,
                    released_by INTEGER NOT NULL,
                    date_released TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (item_id) REFERENCES items(item_id),
                    FOREIGN KEY (claimant_id) REFERENCES users(id),
                    FOREIGN KEY (released_by) REFERENCES users(id)
                )
                """;

        // 5. activity_logs (SQL TABLE)
        String logsTable = """
                CREATE TABLE IF NOT EXISTS activity_logs (
                    log_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    action_type TEXT NOT NULL,
                    description TEXT,
                    log_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (user_id) REFERENCES users(id)
                )
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(usersTable);
            stmt.execute(categoriesTable);
            stmt.execute(itemsTable);
            stmt.execute(claimsTable);
            stmt.execute(logsTable);

            System.out.println("Database initialized.");

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }
}