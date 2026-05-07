package org.example.controller;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.example.service.ApiService;

public class LoginController {

    private static final ApiService apiService = new ApiService();

    // city, country, gradient-from, gradient-to, image-resource-path
    private static final String[][] CITIES = {
        {"Sarajevo",   "Bosnia & Herzegovina", "#0D1B2A", "#1B3A5C", "/images/sarajevo.jpg"},
        {"Dubrovnik",  "Croatia",              "#0D2A1B", "#1B5C3A", "/images/dubrovnik.jpg"},
        {"Kotor",      "Montenegro",           "#1B0D2A", "#3A1B5C", "/images/kotor.jpg"},
        {"Mostar",     "Bosnia & Herzegovina", "#2A0D0D", "#5C1B1B", "/images/mostar.jpg"},
        {"Ljubljana",  "Slovenia",             "#0D2A2A", "#1B5C5C", "/images/ljubljana.jpg"},
        {"Belgrade",   "Serbia",               "#2A1B0D", "#5C3A1B", "/images/belgrade.jpg"},
        {"Ohrid",      "North Macedonia",      "#0D1B2A", "#1B3A6E", "/images/ohrid.jpg"},
        {"Tirana",     "Albania",              "#2A0D1B", "#5C1B3A", "/images/tirana.jpg"},
    };

    public static String[] showLoginScreen() {
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);

        StackPane root = new StackPane();
        root.setPrefSize(860, 520);

        HBox mainContent = new HBox();
        mainContent.setPrefSize(860, 520);

        // ── LEFT PANEL ────────────────────────────────────────────────
        VBox leftPanel = new VBox();
        leftPanel.setPrefWidth(430);
        leftPanel.setAlignment(Pos.CENTER);
        leftPanel.setPadding(new Insets(50, 50, 50, 50));
        leftPanel.setStyle("-fx-background-color: linear-gradient(to bottom right, #1A237E, #3949AB);");

        Label title = new Label("Aktour ViaBalkan");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 26px; -fx-font-weight: bold;");

        Label subtitle = new Label("Management System");
        subtitle.setStyle("-fx-text-fill: #B0BEC5; -fx-font-size: 12px;");

        Region spacerTop = new Region();
        spacerTop.setPrefHeight(32);

        Label userLabel = new Label("Username");
        userLabel.setStyle("-fx-text-fill: #B0BEC5; -fx-font-size: 12px;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username");
        usernameField.setPrefWidth(300);
        usernameField.setStyle("-fx-background-radius: 6; -fx-padding: 9; -fx-font-size: 13px;");

        Region gap1 = new Region();
        gap1.setPrefHeight(10);

        Label passLabel = new Label("Password");
        passLabel.setStyle("-fx-text-fill: #B0BEC5; -fx-font-size: 12px;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.setPrefWidth(300);
        passwordField.setStyle("-fx-background-radius: 6; -fx-padding: 9; -fx-font-size: 13px;");

        Region gap2 = new Region();
        gap2.setPrefHeight(14);

        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: #EF9A9A; -fx-font-size: 11px;");
        errorLabel.setMaxWidth(300);
        errorLabel.setWrapText(true);

        Button loginBtn = new Button("Login");
        loginBtn.setPrefWidth(300);
        loginBtn.setStyle("-fx-background-color: #4FC3F7; -fx-text-fill: #1A237E; -fx-font-weight: bold; " +
                "-fx-background-radius: 6; -fx-padding: 10; -fx-font-size: 13px;");

        VBox formBox = new VBox(0,
                userLabel, usernameField,
                gap1,
                passLabel, passwordField,
                gap2,
                errorLabel, loginBtn);
        formBox.setMaxWidth(300);
        formBox.setAlignment(Pos.CENTER_LEFT);

        leftPanel.getChildren().addAll(title, subtitle, spacerTop, formBox);

        // ── RIGHT PANEL (Slideshow) ───────────────────────────────────
        // slides sit in a StackPane that grows, dots are pinned below it
        StackPane slidesPane = new StackPane();
        VBox.setVgrow(slidesPane, javafx.scene.layout.Priority.ALWAYS);
        slidesPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        StackPane[] slides = new StackPane[CITIES.length];
        for (int i = 0; i < CITIES.length; i++) {
            StackPane slide = buildSlide(CITIES[i][0], CITIES[i][1], CITIES[i][2], CITIES[i][3], CITIES[i][4]);
            slide.setOpacity(i == 0 ? 1.0 : 0.0);
            slide.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            slides[i] = slide;
            slidesPane.getChildren().add(slide);
        }

        HBox dots = new HBox(6);
        dots.setAlignment(Pos.CENTER);
        dots.setPrefHeight(22);
        dots.setStyle("-fx-background-color: #0D1B2A;");
        for (int i = 0; i < CITIES.length; i++) {
            Label dot = new Label("●");
            dot.setStyle(i == 0
                    ? "-fx-text-fill: white; -fx-font-size: 9px;"
                    : "-fx-text-fill: rgba(255,255,255,0.35); -fx-font-size: 9px;");
            dots.getChildren().add(dot);
        }

        VBox rightPanel = new VBox();
        rightPanel.setPrefSize(430, 520);
        rightPanel.getChildren().addAll(slidesPane, dots);

        int[] current = {0};
        Timeline slideshow = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
            int prev = current[0];
            current[0] = (current[0] + 1) % CITIES.length;
            int next = current[0];

            FadeTransition out = new FadeTransition(Duration.millis(400), slides[prev]);
            out.setToValue(0.0);
            out.play();

            FadeTransition in = new FadeTransition(Duration.millis(400), slides[next]);
            in.setToValue(1.0);
            in.play();

            for (int i = 0; i < dots.getChildren().size(); i++) {
                ((Label) dots.getChildren().get(i)).setStyle(i == next
                        ? "-fx-text-fill: white; -fx-font-size: 9px;"
                        : "-fx-text-fill: rgba(255,255,255,0.35); -fx-font-size: 9px;");
            }
        }));
        slideshow.setCycleCount(Timeline.INDEFINITE);
        slideshow.play();

        mainContent.getChildren().addAll(leftPanel, rightPanel);

        // ── WINDOW CONTROLS (top-right) ───────────────────────────────
        Button minBtn = new Button("─");
        Button closeBtn = new Button("✕");

        String btnBase = "-fx-background-color: transparent; -fx-text-fill: white; " +
                "-fx-font-size: 13px; -fx-padding: 4 10; -fx-cursor: hand;";
        minBtn.setStyle(btnBase);
        closeBtn.setStyle(btnBase);

        minBtn.setOnMouseEntered(e -> minBtn.setStyle(btnBase + "-fx-background-color: rgba(255,255,255,0.15);"));
        minBtn.setOnMouseExited(e -> minBtn.setStyle(btnBase));
        closeBtn.setOnMouseEntered(e -> closeBtn.setStyle(btnBase + "-fx-background-color: #E53935;"));
        closeBtn.setOnMouseExited(e -> closeBtn.setStyle(btnBase));

        minBtn.setOnAction(e -> stage.setIconified(true));
        closeBtn.setOnAction(e -> { slideshow.stop(); stage.close(); });

        HBox winControls = new HBox(2, minBtn, closeBtn);
        winControls.setAlignment(Pos.TOP_RIGHT);
        StackPane.setAlignment(winControls, Pos.TOP_RIGHT);
        StackPane.setMargin(winControls, new Insets(6, 6, 0, 0));

        // ── WINDOW DRAG ───────────────────────────────────────────────
        double[] dragOffset = {0, 0};
        mainContent.setOnMousePressed(e -> {
            dragOffset[0] = e.getScreenX() - stage.getX();
            dragOffset[1] = e.getScreenY() - stage.getY();
        });
        mainContent.setOnMouseDragged(e -> {
            stage.setX(e.getScreenX() - dragOffset[0]);
            stage.setY(e.getScreenY() - dragOffset[1]);
        });

        root.getChildren().addAll(mainContent, winControls);

        // ── LOGIN LOGIC ───────────────────────────────────────────────
        String[][] result = {null};

        Runnable doLogin = () -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            if (username.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Username and password cannot be empty.");
                return;
            }
            loginBtn.setDisable(true);
            loginBtn.setText("Signing in...");
            new Thread(() -> {
                try {
                    String[] auth = apiService.login(username, password);
                    ApiService.setToken(auth[0]);
                    result[0] = auth;
                    javafx.application.Platform.runLater(() -> {
                        slideshow.stop();
                        stage.close();
                    });
                } catch (Exception ex) {
                    javafx.application.Platform.runLater(() -> {
                        errorLabel.setText(ex.getMessage());
                        loginBtn.setDisable(false);
                        loginBtn.setText("Login");
                    });
                }
            }).start();
        };

        loginBtn.setOnAction(e -> doLogin.run());
        passwordField.setOnAction(e -> doLogin.run());
        usernameField.setOnAction(e -> passwordField.requestFocus());

        Scene scene = new Scene(root);
        stage.setScene(scene);

        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        stage.setX((screen.getWidth() - 860) / 2);
        stage.setY((screen.getHeight() - 520) / 2);

        stage.showAndWait();
        return result[0];
    }

    private static StackPane buildSlide(String city, String country, String c1, String c2, String imagePath) {
        StackPane slide = new StackPane();
        slide.setStyle("-fx-background-color: linear-gradient(to bottom right, " + c1 + ", " + c2 + ");");

        java.net.URL imgUrl = LoginController.class.getResource(imagePath);
        if (imgUrl != null) {
            javafx.scene.image.ImageView iv = new javafx.scene.image.ImageView(
                    new javafx.scene.image.Image(imgUrl.toString(), 430, 500, false, true));
            iv.setPreserveRatio(false);
            iv.fitWidthProperty().bind(slide.widthProperty());
            iv.fitHeightProperty().bind(slide.heightProperty());
            slide.getChildren().add(iv);
        }

        // bottom gradient overlay — no box, just fade to dark at bottom
        javafx.scene.layout.Region bottomGrad = new javafx.scene.layout.Region();
        bottomGrad.setStyle("-fx-background-color: linear-gradient(to top, rgba(0,0,0,0.75) 0%, transparent 45%);");
        slide.getChildren().add(bottomGrad);

        VBox info = new VBox(2);
        info.setAlignment(Pos.CENTER_LEFT);
        StackPane.setAlignment(info, Pos.BOTTOM_LEFT);
        StackPane.setMargin(info, new Insets(0, 0, 28, 24));

        Label cityLabel = new Label(city);
        cityLabel.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-weight: bold;");

        Label countryLabel = new Label(country);
        countryLabel.setStyle("-fx-text-fill: #CFD8DC; -fx-font-size: 12px;");

        info.getChildren().addAll(cityLabel, countryLabel);
        slide.getChildren().add(info);
        return slide;
    }
}
