package com.scc.dao;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseInitializer {

    public static void initialize() {

        String usersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    full_name TEXT NOT NULL,
                    contact_number TEXT NOT NULL,
                    roles TEXT NOT NULL,
                    status TEXT NOT NULL
                )
                """;

        String categoriesTable = """
                CREATE TABLE IF NOT EXISTS categories (
                    category_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    category_name TEXT NOT NULL UNIQUE
                )
                """;

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
                    FOREIGN KEY (reported_by) REFERENCES users(user_id)
                )
                """;

        String claimsTable = """
                CREATE TABLE IF NOT EXISTS claims (
                    claim_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    item_id INTEGER NOT NULL,
                    claimant_id INTEGER NOT NULL,
                    id_presented TEXT NOT NULL,
                    released_by INTEGER NOT NULL,
                    date_released TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (item_id) REFERENCES items(item_id),
                    FOREIGN KEY (claimant_id) REFERENCES users(user_id),
                    FOREIGN KEY (released_by) REFERENCES users(user_id)
                )
                """;

        String activityLogsTable = """
                CREATE TABLE IF NOT EXISTS activity_logs (
                    log_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    action_type TEXT NOT NULL,
                    description TEXT,
                    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
                )
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(usersTable);
            stmt.execute(categoriesTable);
            stmt.execute(itemsTable);
            stmt.execute(claimsTable);
            stmt.execute(activityLogsTable);

            System.out.println("Database initialized.");

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }
}