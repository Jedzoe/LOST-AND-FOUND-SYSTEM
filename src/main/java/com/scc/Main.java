package com.scc;

import com.scc.dao.DatabaseInitializer;
import com.scc.dao.UserDao;
import com.scc.ui.Authentication;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // 1. Make sure the database and tables exist
        DatabaseInitializer.initialize();

        // 2. Create ONE UserDao and ONE Scanner and share them
        UserDao userDao = new UserDao();
        Scanner sc = new Scanner(System.in);

        // 3. Hand over to the login/register screens
        new Authentication(userDao, sc).start();
    }
}