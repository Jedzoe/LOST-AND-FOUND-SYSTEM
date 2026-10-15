package com.scc;

import com.scc.dao.UserDao;
import com.scc.model.User;
import com.scc.ui.AdminMenu;
import com.scc.ui.StaffMenu;
import com.scc.ui.UserMenu;

import java.util.Scanner;

public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static final UserDao userDao = new UserDao();

    public static void main(String[] args) {

        boolean running = true;

        while (running) {
            System.out.println();
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
                    System.out.print("Enter a new username: ");
                    String user = sc.next();
                    System.out.print("Enter a new password: ");
                    String pass = sc.next();
                    System.out.print("Select role [1] STAFF [2] USER: ");
                    String roleChoice = sc.next();

                    String role;
                    switch (roleChoice) {
                        case "1":
                            role = "STAFF";
                            break;
                        case "2":
                            role = "USER";
                            break;
                        default:
                            role = null;
                    }

                    if (role != null) {
                        userDao.registerUser(user, pass, role, "PENDING");
                    } else {
                        System.out.println("Invalid role selection. Please try again.");
                    }
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

    private static void handleLogin() {
        System.out.print("Enter username: ");
        String username = sc.next();
        System.out.print("Enter password: ");
        String password = sc.next();

        User user = userDao.login(username, password);

        if (user != null) {
            System.out.print("Login successful! Welcome, " + user.username() + " | ROLE: " + user.roles());
            switch (user.roles()) {
                case "ADMIN" -> new AdminMenu(userDao, sc).display(user);
                case "STAFF" -> new StaffMenu(userDao, sc).display(user);
                case "USER" -> new UserMenu(userDao, sc).display(user);
                default -> System.out.println("Unknown role. Access denied.");
            }
        }
    }
}