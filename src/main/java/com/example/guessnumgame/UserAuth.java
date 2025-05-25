package com.example.guessnumgame;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class UserAuth {
    private final UserManager userManager;
    private final Stage stage;
    private final Runnable onLoginSuccess;

    public UserAuth(Stage stage, UserManager userManager, Runnable onLoginSuccess) {
        this.stage = stage;
        this.userManager = userManager;
        this.onLoginSuccess = onLoginSuccess;
    }

    public void showLogin() {
        VBox authLayout = new VBox(20);
        authLayout.setAlignment(Pos.CENTER);
        authLayout.setPadding(new Insets(20));
        authLayout.setStyle("-fx-background-color: #FFBAEA;");

        Label titleLabel = new Label("GUESS THE NUMBER");
        titleLabel.setStyle("-fx-font-size: 45px; -fx-font-weight: bold; -fx-font-family: 'Party LET'; -fx-text-fill: #000000;");

        VBox loginBox = new VBox(10);
        loginBox.setAlignment(Pos.CENTER);
        loginBox.setStyle("-fx-background-color: #E85FAD; -fx-padding: 20; -fx-background-radius: 10;");

        Label loginLabel = new Label("Login");
        loginLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-font-family: 'Arial'; -fx-text-fill: #000000;");
        
        TextField username = new TextField();
        username.setPromptText("Username");
        username.setMaxWidth(200);
        
        PasswordField password = new PasswordField();
        password.setPromptText("Password");
        password.setMaxWidth(200);

        Label statusLabel = new Label("");
        statusLabel.setStyle("-fx-text-fill: #000000; -fx-font-size: 14px;");

        Button loginButton = createStyledButton("LOGIN");
        Button registerButton = createStyledButton("REGISTER FOR NEW USER");
        
        loginBox.getChildren().addAll(loginLabel, username, password, loginButton, new Separator(), registerButton);
        authLayout.getChildren().addAll(titleLabel, loginBox, statusLabel);

        loginButton.setOnAction(e -> {
            if (username.getText().isEmpty() || password.getText().isEmpty()) {
                showError(statusLabel, "Please fill in all fields");
            } else if (userManager.login(username.getText(), password.getText())) {
                onLoginSuccess.run();
            } else {
                showError(statusLabel, "Invalid username or password");
            }
        });

        registerButton.setOnAction(e -> showRegister());

        Scene scene = new Scene(authLayout, 800, 500);
        stage.setScene(scene);
        stage.show();
    }

    private void showRegister() {
        VBox registerLayout = new VBox(20);
        registerLayout.setAlignment(Pos.CENTER);
        registerLayout.setPadding(new Insets(20));
        registerLayout.setStyle("-fx-background-color: #FFBAEA;");

        Label titleLabel = new Label("REGISTER NEW USER");
        titleLabel.setStyle("-fx-font-size: 45px; -fx-font-weight: bold; -fx-font-family: 'Party LET'; -fx-text-fill: #000000;");

        VBox registerBox = new VBox(10);
        registerBox.setAlignment(Pos.CENTER);
        registerBox.setStyle("-fx-background-color: #E85FAD; -fx-padding: 20; -fx-background-radius: 10;");

        TextField username = new TextField();
        username.setPromptText("Username");
        username.setMaxWidth(200);
        
        PasswordField password = new PasswordField();
        password.setPromptText("Password");
        password.setMaxWidth(200);

        Label statusLabel = new Label("");
        statusLabel.setStyle("-fx-text-fill: #000000; -fx-font-size: 14px;");

        Button registerButton = createStyledButton("REGISTER");
        Button backButton = createStyledButton("BACK TO LOGIN");

        registerBox.getChildren().addAll(username, password, registerButton, new Separator(), backButton);
        registerLayout.getChildren().addAll(titleLabel, registerBox, statusLabel);

        registerButton.setOnAction(e -> {
            if (userManager.register(username.getText(), password.getText())) {
                showSuccess(statusLabel, "Registration successful!");
                showLogin();
            } else {
                showError(statusLabel, "Registration failed. Username might be taken or fields are empty.");
            }
        });

        backButton.setOnAction(e -> showLogin());

        Scene scene = new Scene(registerLayout, 800, 500);
        stage.setScene(scene);
        stage.show();
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #555555; -fx-text-fill: white;");
        
        button.setOnMouseEntered(e -> 
            button.setStyle("-fx-background-color: #666666; -fx-text-fill: white;"));
        
        button.setOnMouseExited(e -> 
            button.setStyle("-fx-background-color: #444444; -fx-text-fill: white;"));
        
        return button;
    }

    private void showError(Label label, String message) {
        label.setText(message);
        label.setStyle("-fx-text-fill: red;");
    }

    private void showSuccess(Label label, String message) {
        label.setText(message);
        label.setStyle("-fx-text-fill: green;");
    }
} 