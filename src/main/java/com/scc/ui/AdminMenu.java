package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;

import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    private final UserDao userDao;
    private final Scanner sc;

    public AdminMenu(UserDao userDao, Scanner sc) {
        this.userDao = userDao;
        this.sc = sc;
    }

    public void display(User admin) {
        boolean running = true;

        while (running) {
            System.out.println();
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
                case "2" -> manageUsers();
                case "1", "3", "4", "5", "6" -> System.out.println("Coming soon.");
                case "0" -> {
                    System.out.println("You have been logged out.");
                    running = false;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // Submenu for option [2]: the user management features.
    private void manageUsers() {
        boolean running = true;

        while (running) {
            System.out.println();
            System.out.println("========================================");
            System.out.println("          MANAGE USERS & ROLES          ");
            System.out.println("========================================");
            System.out.println(" [1] View All System Users");
            System.out.println(" [2] View Pending Registrations");
            System.out.println(" [3] Approve User Account");
            System.out.println(" [0] Back");
            System.out.println("----------------------------------------");
            System.out.print("Choose an option: ");
            String choice = sc.next();

            switch (choice) {
                case "1" -> viewAllUsers();
                case "2" -> viewPendingUsers();
                case "3" -> approveUser();
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

    private void approveUser() {
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
                done = true;
            } else {
                System.out.println("Could not approve the account. Please try again.");
                done = true;
            }
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