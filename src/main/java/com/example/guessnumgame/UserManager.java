package com.example.guessnumgame;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import javafx.stage.Stage;

public class UserManager {
    private static final String DATA_FILE = "users.dat";
    private Map<String, User> users;
    private User currentUser;
    private Stage previousStage;

    public UserManager() {
        users = new HashMap<>();
        loadUsers();
    }

    public void setPreviousStage(Stage stage) {
        this.previousStage = stage;
    }

    public Stage getPreviousStage() {
        return previousStage;
    }

    public boolean register(String username, String password) {
        if (username.isEmpty() || password.isEmpty() || users.containsKey(username)) {
            return false;
        }
        users.put(username, new User(username, password));
        saveUsers();
        return true;
    }

    public boolean login(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            return true;
        }
        return false;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void updateCurrentUserScore(int newScore) {
        if (currentUser != null) {
            currentUser.updateHighScore(newScore);
            saveUsers();
        }
    }

    private void loadUsers() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            users = new HashMap<>();
            return;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            users = (Map<String, User>) ois.readObject();
        } catch (Exception e) {
            users = new HashMap<>();
        }
    }

    private void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Integer> getAllHighScores() {
        Map<String, Integer> highScores = new HashMap<>();
        for (User user : users.values()) {
            highScores.put(user.getUsername(), user.getHighScore());
        }
        return highScores;
    }
} 