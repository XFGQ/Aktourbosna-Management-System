package org.example.controller;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.service.ApiService;

public class LoginController {

    private static final ApiService apiService = new ApiService();

    public static String[] showLoginScreen() {
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #1A237E, #3949AB);");
        root.setPrefWidth(420);

        Label title = new Label("Aktour ViaBalkan");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 26px; -fx-font-weight: bold;");

        Label subtitle = new Label("Management System");
        subtitle.setStyle("-fx-text-fill: #B0BEC5; -fx-font-size: 13px;");

        VBox spacer = new VBox();
        spacer.setPrefHeight(10);

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(12);
        form.setAlignment(Pos.CENTER);

        Label userLabel = new Label("Username");
        userLabel.setStyle("-fx-text-fill: #B0BEC5; -fx-font-size: 12px;");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username");
        usernameField.setPrefWidth(260);
        usernameField.setStyle("-fx-background-radius: 6; -fx-padding: 8;");

        Label passLabel = new Label("Password");
        passLabel.setStyle("-fx-text-fill: #B0BEC5; -fx-font-size: 12px;");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.setPrefWidth(260);
        passwordField.setStyle("-fx-background-radius: 6; -fx-padding: 8;");

        form.add(userLabel, 0, 0);
        form.add(usernameField, 0, 1);
        form.add(passLabel, 0, 2);
        form.add(passwordField, 0, 3);

        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: #EF9A9A; -fx-font-size: 11px;");

        Button loginBtn = new Button("Login");
        loginBtn.setStyle("-fx-background-color: #4FC3F7; -fx-text-fill: #1A237E; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8 40;");
        loginBtn.setPrefWidth(260);

        String[][] result = {null};

        Runnable doLogin = () -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            if (username.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Kullanıcı adı ve şifre boş olamaz.");
                return;
            }
            loginBtn.setDisable(true);
            loginBtn.setText("Giriş yapılıyor...");
            new Thread(() -> {
                try {
                    String[] auth = apiService.login(username, password);
                    ApiService.setToken(auth[0]);
                    result[0] = auth;
                    javafx.application.Platform.runLater(stage::close);
                } catch (Exception e) {
                    javafx.application.Platform.runLater(() -> {
                        errorLabel.setText(e.getMessage());
                        loginBtn.setDisable(false);
                        loginBtn.setText("Login");
                    });
                }
            }).start();
        };

        loginBtn.setOnAction(e -> doLogin.run());
        passwordField.setOnAction(e -> doLogin.run());
        usernameField.setOnAction(e -> passwordField.requestFocus());

        HBox btnBox = new HBox(loginBtn);
        btnBox.setAlignment(Pos.CENTER);

        root.getChildren().addAll(title, subtitle, spacer, form, errorLabel, btnBox);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.showAndWait();

        return result[0];
    }
}
