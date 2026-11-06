package com.scc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ItemDao {

    // Returns the new item ID, or -1 if it failed.
    public int addFoundItem(int staffId, String name, String description, String location) {
        String catInsert = "INSERT OR IGNORE INTO categories (category_name) VALUES (?)";
        String catSelect = "SELECT category_id FROM categories WHERE category_name = ?";
        String sql = "INSERT INTO items (item_name, description, category_id, location, status, reported_by) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {

            try (PreparedStatement p = conn.prepareStatement(catInsert)) {
                p.setString(1, "General");
                p.executeUpdate();
            }

            int categoryId;
            try (PreparedStatement p = conn.prepareStatement(catSelect)) {
                p.setString(1, "General");
                try (ResultSet rs = p.executeQuery()) {
                    rs.next();
                    categoryId = rs.getInt(1);
                }
            }

            try (PreparedStatement p = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                p.setString(1, name);
                p.setString(2, description);
                p.setInt(3, categoryId);
                p.setString(4, location);
                p.setString(5, "FOUND");
                p.setInt(6, staffId);
                p.executeUpdate();

                try (ResultSet keys = p.getGeneratedKeys()) {
                    if (keys.next()) {
                        return keys.getInt(1);
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error occurred while adding item: " + e.getMessage());
        }
        return -1;
    }

    // Returns the current status, or null if the item doesn't exist.
    public String getItemStatus(int itemId) {
        String sql = "SELECT status FROM items WHERE item_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, itemId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("status");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error occurred while getting item status: " + e.getMessage());
        }
        return null;
    }

    public boolean updateItemStatus(int itemId, String newStatus) {
        String sql = "UPDATE items SET status = ? WHERE item_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newStatus.toUpperCase());
            pstmt.setInt(2, itemId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error occurred while updating item status: " + e.getMessage());
        }
        return false;
    }
}