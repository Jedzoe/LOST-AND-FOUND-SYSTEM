package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;
import com.scc.dao.ActivityLogDao;
import com.scc.model.ActivityLog;

import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    private final UserDao userDao;
    private final ActivityLogDao logDao = new ActivityLogDao();
    private final Scanner sc;

    public AdminMenu(UserDao userDao, Scanner sc) {
        this.userDao = userDao;
        this.sc = sc;
    }

    public void display(User admin) {
        boolean running = true;

        while (running) {
            System.out.println("========================================");
            System.out.println("               ADMIN MENU               ");
            System.out.println("========================================");
            System.out.println(" [1] Manage Items");
            System.out.println(" [2] Manage Users & Roles");
            System.out.println(" [3] View Lost Item Reports");
            System.out.println(" [4] View Claim Records");
            System.out.println(" [5] View Summary Report");
            System.out.println(" [6] View Activity & Audit Logs");
            System.out.println(" [0] Logout");
            System.out.println("----------------------------------------");
            System.out.print("Choose an option: ");
            String choice = sc.next();

            switch (choice) {
                case "2" -> manageUsers(admin);
                case "6" -> viewAuditLogs();
                case "1", "3", "4", "5" -> System.out.println("Coming soon.");
                case "0" -> {
                    System.out.println("You have been logged out.");
                    running = false;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // Submenu for option [2]: the user management features.
    private void manageUsers(User admin) {
        boolean running = true;

        while (running) {
            System.out.println();
            System.out.println("========================================");
            System.out.println("          MANAGE USERS & ROLES          ");
            System.out.println("========================================");
            System.out.println(" [1] View All System Users");
            System.out.println(" [2] View Pending Registrations");
            System.out.println(" [3] Approve User Account");
            System.out.println(" [4] Update User Role and Status");
            System.out.println(" [0] Back");
            System.out.println("----------------------------------------");
            System.out.print("Choose an option: ");
            String choice = sc.next();

            switch (choice) {
                case "1" -> viewAllUsers();
                case "2" -> viewPendingUsers();
                case "3" -> approveUser(admin);
                case "4" -> updateUserRoleAndStatus(admin);
                case "0" -> running = false;
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void viewAllUsers() {
        System.out.println();
        System.out.println("ALL SYSTEM USERS");
        printUserTable(userDao.getAllUsers());
    }

    private void viewPendingUsers() {
        System.out.println();
        System.out.println("PENDING REGISTRATIONS");
        printUserTable(userDao.getPendingUsers());
    }

    private void approveUser(User admin) {
        List<User> pending = userDao.getPendingUsers();

        System.out.println();
        System.out.println("APPROVE USER ACCOUNT");
        printUserTable(pending);

        if (pending.isEmpty()) {
            return; // nothing to approve
        }

        boolean done = false;
        while (!done) {
            System.out.print("Enter User ID to approve (0 to cancel): ");
            String input = sc.next();

            int userId;
            try {
                userId = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
                continue;
            }

            if (userId == 0) {
                System.out.println("Approval cancelled.");
                return;
            }

            // Make sure the ID is really in the pending list.
            User target = null;
            for (User u : pending) {
                if (u.id() == userId) {
                    target = u;
                    break;
                }
            }

            if (target == null) {
                System.out.println("ID " + userId + " is not in the pending list. Try again.");
            } else if (userDao.approveUser(userId)) {
                System.out.println("Success! " + target.username() + " (" + target.roles() + ") has been approved.");
                logDao.logAction(admin.id(), "USER_APPROVED",
                        "Admin [" + admin.id() + "] approved User ID " + userId);
                done = true;
            } else {
                System.out.println("Could not approve the account. Please try again.");
                done = true;
            }
        }
    }

    private void updateUserRoleAndStatus(User admin) {
        List<User> allUsers = userDao.getAllUsers();

        System.out.println();
        System.out.println("UPDATE USER ROLE AND STATUS");
        printUserTable(allUsers);

        User target = null;
        while (target == null) {
            int targetUserId = readIntInput("Enter User ID to update (0 to cancel): ");

            if (targetUserId == 0) {
                System.out.println("Update cancelled.");
                return;
            }

            if (targetUserId == admin.id()) {
                System.out.println("You cannot update your own account. Please choose another user.");
                continue;
            }

            for (User u : allUsers) {
                if (u.id() == targetUserId) {
                    target = u;
                    break;
                }
            }

            if (target == null) {
                System.out.println("ID " + targetUserId + " is not in the user list. Try again.");
            }
        }

        String newRole = readValidRole();
        if (newRole == null) {
            System.out.println("Update cancelled.");
            return;
        }

        String newStatus = readValidStatus();
        if (newStatus == null) {
            System.out.println("Update cancelled.");
            return;
        }

        if (userDao.updateUserRoleAndStatus(target.id(), newRole, newStatus)) {
            System.out.println("Success! " + target.username() + " has been updated to Role: " + newRole + ", Status: " + newStatus);
            logDao.logAction(admin.id(), "USER_UPDATED",
                    "Admin [" + admin.id() + "] updated User " + target.id() + ": " + newRole + "/" + newStatus);
        } else {
            System.out.println("Could not update the account. Please try again.");
        }
    }

    private void viewAuditLogs() {
        boolean running = true;

        while (running) {
            System.out.println();
            System.out.println("========================================");
            System.out.println("           SYSTEM AUDIT LOGS            ");
            System.out.println("========================================");
            System.out.println(" [1] View All System Logs");
            System.out.println(" [2] Filter Logs by User ID");
            System.out.println(" [0] Back");
            System.out.println("----------------------------------------");
            System.out.print("Choose an option: ");
            String choice = sc.next();

            switch (choice) {
                case "1" -> printLogTable(logDao.getAllLogs());
                case "2" -> {
                    int userId = readIntInput("Enter User ID to filter: ");
                    printLogTable(logDao.getLogsByUserId(userId));
                }
                case "0" -> running = false;
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void printLogTable(List<ActivityLog> logs) {
        System.out.println();
        if (logs.isEmpty()) {
            System.out.println("No logs to show.");
            return;
        }

        String format = "%-5s | %-8s | %-18s | %-35s | %-20s%n";
        String line = "-".repeat(5 + 3 + 8 + 3 + 18 + 3 + 35 + 3 + 20);

        System.out.println(line);
        System.out.printf(format, "ID", "User ID", "Action Type", "Description", "Timestamp");
        System.out.println(line);
        for (ActivityLog l : logs) {
            System.out.printf(format, l.logId(), l.userId(), l.actionType(),
                    shorten(l.description(), 35), l.timestamp());
        }
        System.out.println(line);
    }

    // Keeps long descriptions from breaking the column alignment.
    private String shorten(String text, int max) {
        if (text == null) return "";
        return text.length() <= max ? text : text.substring(0, max - 3) + "...";
    }

    private int readIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.next();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    private String readValidRole() {
        while (true) {
            System.out.print("Enter new Role (ADMIN, STAFF, USER) or 0 to cancel: ");
            String input = sc.next().trim().toUpperCase();
            if (input.equals("0")) return null;
            if (input.equals("ADMIN") || input.equals("STAFF") || input.equals("USER")) return input;
            System.out.println("Invalid input. Please enter a valid role.");
        }
    }

    private String readValidStatus() {
        while (true) {
            System.out.print("Enter new Status (APPROVED, PENDING, REJECTED) or 0 to cancel: ");
            String input = sc.next().trim().toUpperCase();
            if (input.equals("0")) return null;
            if (input.equals("APPROVED") || input.equals("PENDING") || input.equals("REJECTED")) return input;
            System.out.println("Invalid input. Please enter a valid status.");
        }
    }

    // Prints any list of users as a neat table with aligned columns.
    private void printUserTable(List<User> users) {
        if (users.isEmpty()) {
            System.out.println("No users to show.");
            return;
        }

        String border = "+-------+-----------------+--------------+------------+";
        System.out.println(border);
        System.out.printf("| %-5s | %-15s | %-12s | %-10s |%n", "ID", "Username", "Role", "Status");
        System.out.println(border);
        for (User u : users) {
            System.out.printf("| %-5d | %-15s | %-12s | %-10s |%n", u.id(), u.username(), u.roles(), u.status());
        }
        System.out.println(border);
    }
}