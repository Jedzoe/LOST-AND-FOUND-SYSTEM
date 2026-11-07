package com.scc.ui;

import com.scc.dao.ActivityLogDao;
import com.scc.dao.ItemDao;
import com.scc.dao.UserDao;
import com.scc.model.User;

import java.util.Scanner;

public class StaffMenu {
    private final UserDao userDao;
    private final ItemDao itemDao = new ItemDao();
    private final ActivityLogDao logDao = new ActivityLogDao();
    private final Scanner sc;

    public StaffMenu(UserDao userDao, Scanner sc) {
        this.userDao = userDao;
        this.sc = sc;
    }

    public void display(User user) {
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("========================================");
            System.out.println("               STAFF MENU               ");
            System.out.println("========================================");
            System.out.println(" [1] Add Found Item");
            System.out.println(" [2] Update Item Status");
            System.out.println(" [3] Process Claim / Release Item");
            System.out.println(" [4] View Lost Item Reports");
            System.out.println(" [5] View Claim Records");
            System.out.println(" [0] Logout");
            System.out.println("----------------------------------------");
            System.out.print("Choose an option: ");
            String choice = sc.next();

            switch (choice) {
                case "1":
                    addFoundItem(user);
                    break;
                case "2":
                    updateItemStatus(user);
                    break;
                case "3":
                case "4":
                case "5":
                    System.out.println("Coming soon.");
                    break;
                case "0":
                    System.out.println("You have been logged out.");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void addFoundItem(User staff) {
        sc.nextLine(); // clears the leftover newline from the menu choice

        System.out.print("Enter item name: ");
        String name = sc.nextLine().trim();
        System.out.print("Enter description: ");
        String description = sc.nextLine().trim();
        System.out.print("Enter location found: ");
        String location = sc.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("Item name cannot be empty.");
            return;
        }

        int itemId = itemDao.addFoundItem(staff.id(), name, description, location);
        if (itemId > 0) {
            System.out.println("Success! Item #" + itemId + " (" + name + ") has been added.");
            logDao.logAction(staff.id(), "ITEM_FOUND_ADDED",
                    "Staff [" + staff.id() + "] added found item #" + itemId + ": " + name);
        } else {
            System.out.println("Could not add the item. Please try again.");
        }
    }

    private void updateItemStatus(User staff) {
        System.out.print("Enter Item ID to update (0 to cancel): ");
        String input = sc.next();

        int itemId;
        try {
            itemId = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            return;
        }
        if (itemId == 0) {
            System.out.println("Update cancelled.");
            return;
        }

        String oldStatus = itemDao.getItemStatus(itemId);
        if (oldStatus == null) {
            System.out.println("Item #" + itemId + " does not exist.");
            return;
        }

        String newStatus;
        while (true) {
            System.out.print("Enter new status (LOST, FOUND, CLAIMED) or 0 to cancel: ");
            newStatus = sc.next().trim().toUpperCase();
            if (newStatus.equals("0")) {
                System.out.println("Update cancelled.");
                return;
            }
            if (newStatus.equals("LOST") || newStatus.equals("FOUND") || newStatus.equals("CLAIMED")) {
                break;
            }
            System.out.println("Invalid status. Please try again.");
        }

        if (itemDao.updateItemStatus(itemId, newStatus)) {
            System.out.println("Success! Item #" + itemId + " is now " + newStatus + ".");
            logDao.logAction(staff.id(), "ITEM_STATUS_UPDATED",
                    "Updated Item #" + itemId + " status from " + oldStatus + " to " + newStatus);
        } else {
            System.out.println("Could not update the item. Please try again.");
        }
    }
}