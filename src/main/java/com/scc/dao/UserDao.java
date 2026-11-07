package com.scc.dao;

import com.scc.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    public UserDao() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            defaultAdmin(conn);
        } catch (SQLException e) {
            System.err.println("Error seeding default admin: " + e.getMessage());
        }
    }

    public void defaultAdmin(Connection conn) {
        String checkSql = "SELECT COUNT(*) FROM users WHERE username = 'admin'";
        String insertsql = "INSERT INTO users (username, password, full_name, contact_number, roles, status) "
                + "VALUES ('admin', 'admin123', 'System Administrator', 'N/A', 'ADMIN', 'APPROVED')";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkSql)) {

            if (rs.next() && rs.getInt(1) == 0) {
                stmt.executeUpdate(insertsql);
                System.out.println("Default admin created successfully.");
            }
        } catch (SQLException e) {
            System.err.println("Error occurred while checking/creating default admin: " + e.getMessage());
        }
    }

    public boolean registerUser(String username, String password, String fullName,
                                String contactNumber, String roles, String status) {

        String sql = "INSERT INTO users (username, password, full_name, contact_number, roles, status) "
                + "VALUES (?, ?, ?, ?, ?, 'PENDING')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, fullName);
            pstmt.setString(4, contactNumber);
            pstmt.setString(5, roles.toUpperCase());
            pstmt.executeUpdate();
            System.out.println("User registered successfully, Waiting for Approval.");
            return true;

        } catch (SQLException e) {
            System.err.println("Error occurred while registering user: " + e.getMessage());
        }
        return false;
    }

    public User login(String username, String password) {
        String sql = "SELECT user_id, username, password, full_name, contact_number, roles, status "
                + "FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String status = rs.getString("status");
                    if ("PENDING".equalsIgnoreCase(status)) {
                        System.out.println("Your account is pending approval. Please wait for admin approval.");
                        return null;
                    }
                    return mapUser(rs);
                } else {
                    System.out.println("Invalid username or password.");
                    return null;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error occurred while logging in: " + e.getMessage());
        }
        return null;
    }

    public List<User> getAllUsers() {
        String sql = "SELECT user_id, username, password, full_name, contact_number, roles, status "
                + "FROM users ORDER BY user_id";
        List<User> users = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapUser(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error occurred while getting all users: " + e.getMessage());
        }
        return users;
    }

    public List<User> getPendingUsers() {
        String sql = "SELECT user_id, username, password, full_name, contact_number, roles, status "
                + "FROM users WHERE status = ? ORDER BY user_id";
        List<User> users = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "PENDING");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapUser(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error occurred while getting pending users: " + e.getMessage());
        }
        return users;
    }

    public boolean approveUser(int userId) {
        String sql = "UPDATE users SET status = 'APPROVED' WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error occurred while approving user: " + e.getMessage());
        }
        return false;
    }

    public boolean updateUserRoleAndStatus(int userId, String role, String status) {
        String sql = "UPDATE users SET roles = ?, status = ? WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, role.toUpperCase());
            pstmt.setString(2, status.toUpperCase());
            pstmt.setInt(3, userId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error occurred while updating user role and status: " + e.getMessage());
        }
        return false;
    }

    private User mapUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("user_id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("roles"),
                rs.getString("status"),
                rs.getString("full_name"),
                rs.getString("contact_number"));
    }
}