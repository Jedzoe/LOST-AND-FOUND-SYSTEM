package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;

import java.util.Scanner;

public class AdminMenu {
    private final UserDao userDao;
    private final Scanner sc;

    public AdminMenu(UserDao userDao, Scanner sc) {
        this.userDao = userDao;
        this.sc = sc;
    }

    public void display(User user) {
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
                case "1":
                    System.out.println("Coming soon.");
                    break;
                case "2":
                    System.out.println("Coming soon.");
                    break;
                case "3":
                    System.out.println("Coming soon.");
                    break;
                case "4":
                    System.out.println("Coming soon.");
                    break;
                case "5":
                    System.out.println("Coming soon.");
                    break;
                case "6":
                    System.out.println("Coming soon.");
                    break;
                case "0":
                    System.out.println("You have been logged out.");
                    running = false; // goes back to the main menu
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}