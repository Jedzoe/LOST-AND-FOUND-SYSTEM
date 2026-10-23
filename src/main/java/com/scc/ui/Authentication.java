package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;

import java.util.Scanner;

public class Authentication {

    private final UserDao userDao;
    private final Scanner sc;

    public Authentication(UserDao userDao, Scanner sc) {
        this.userDao = userDao;
        this.sc = sc;
    }

    public void start() {
        boolean running = true;
        while (running) {
            System.out.println("========================================");
            System.out.println("      LOST AND FOUND ITEM TRACKING      ");
            System.out.println("            AND CLAIM SYSTEM            ");
            System.out.println("========================================");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    handleLogin();
                    break;
                case 2:
                    handleRegister();
                    break;
                case 3:
                    System.out.println("Exiting the system. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void handleLogin() {
        System.out.print("Enter username: ");
        String username = sc.next();
        System.out.print("Enter password: ");
        String password = sc.next();

        User user = userDao.login(username, password);

        if (user != null) {
            System.out.println("Login successful! Welcome, " + user.username() + "ROLE: " + user.roles());

            switch (user.roles()) {
                case "ADMIN":
                    new AdminMenu(userDao, sc).display(user);
                    break;
                case "STAFF":
                    new StaffMenu(userDao, sc).display(user);
                    break;
                case "USER":
                    new UserMenu(userDao, sc).display(user);
                    break;
                default:
                    System.out.println("Unknown role. Access denied.");
            }
        }
    }

    private void handleRegister() {
        System.out.print("Enter a new username: ");
        String username = sc.next();
        System.out.print("Enter a new password: ");
        String password = sc.next();
        System.out.print("Enter role [1] Admin, [2] Staff, [3] User: ");
        int roleChoice = sc.nextInt();

        String role = switch (roleChoice) {
            case 1 -> "Admin";
            case 2 -> "Staff";
            case 3 -> "User";
            default -> null;
        };

        if (role != null) {
            userDao.registerUser(username, password, role, "PENDING");
        } else {
            System.out.println("Invalid role choice. Please try again.");
        }
    }
}

