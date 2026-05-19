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
import javafx.scene.image.ImageView;
import org.example.service.ApiService;
import org.example.service.SessionManager;

public class LoginController {

    private static final ApiService apiService = new ApiService();

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
        root.setPrefSize(980, 540);

        HBox mainContent = new HBox();
        mainContent.setPrefSize(980, 540);

        VBox leftPanel = new VBox();
        leftPanel.setPrefWidth(310);
        leftPanel.setAlignment(Pos.CENTER);
        leftPanel.setPadding(new Insets(50, 30, 50, 30));
        leftPanel.setStyle("-fx-background-color: white;");

        ImageView loginLogo = LogoView.create(52);

        Label title = new Label("Aktour ViaBalkan");
        title.setStyle("-fx-text-fill: #1A237E; -fx-font-size: 22px; -fx-font-weight: bold;");
        title.setWrapText(false);

        Label subtitle = new Label("Management System");
        subtitle.setStyle("-fx-text-fill: #78909C; -fx-font-size: 12px;");

        Region spacerTop = new Region();
        spacerTop.setPrefHeight(32);

        Label userLabel = new Label("Username");
        userLabel.setStyle("-fx-text-fill: #546E7A; -fx-font-size: 12px;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username");
        usernameField.setPrefWidth(250);
        usernameField.setStyle("-fx-background-radius: 6; -fx-padding: 9; -fx-font-size: 13px;");

        Region gap1 = new Region();
        gap1.setPrefHeight(10);

        Label passLabel = new Label("Password");
        passLabel.setStyle("-fx-text-fill: #546E7A; -fx-font-size: 12px;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.setPrefWidth(250);
        passwordField.setStyle("-fx-background-radius: 6; -fx-padding: 9; -fx-font-size: 13px;");

        Region gap2 = new Region();
        gap2.setPrefHeight(14);

        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: #E53935; -fx-font-size: 11px;");
        errorLabel.setMaxWidth(250);
        errorLabel.setWrapText(true);

        Button loginBtn = new Button("Login");
        loginBtn.setPrefWidth(250);
        loginBtn.setStyle("-fx-background-color: #4FC3F7; -fx-text-fill: #1A237E; -fx-font-weight: bold; " +
                "-fx-background-radius: 6; -fx-padding: 10; -fx-font-size: 13px;");

        Label contactLabel = new Label("info@aktourbosna.com");
        contactLabel.setStyle("-fx-text-fill: #90A4AE; -fx-font-size: 11px;");

        Region gap3 = new Region();
        gap3.setPrefHeight(8);

        VBox formBox = new VBox(0,
                userLabel, usernameField,
                gap1,
                passLabel, passwordField,
                gap2,
                errorLabel, loginBtn,
                gap3, contactLabel);
        formBox.setMaxWidth(250);
        formBox.setAlignment(Pos.CENTER_LEFT);

        leftPanel.getChildren().addAll(loginLogo, title, subtitle, spacerTop, formBox);

        StackPane slidesPane = new StackPane();
        VBox.setVgrow(slidesPane, javafx.scene.layout.Priority.ALWAYS);
        slidesPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        javafx.scene.layout.AnchorPane[] slides = new javafx.scene.layout.AnchorPane[CITIES.length];
        for (int i = 0; i < CITIES.length; i++) {
            javafx.scene.layout.AnchorPane slide = buildSlide(CITIES[i][0], CITIES[i][1], CITIES[i][2], CITIES[i][3], CITIES[i][4]);
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
        rightPanel.setPrefSize(670, 540);
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
        winControls.setPickOnBounds(false);
        StackPane.setAlignment(winControls, Pos.TOP_RIGHT);
        StackPane.setMargin(winControls, new Insets(6, 6, 0, 0));

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

        String[][] result = {null};
        Timeline[] watchdog = {null};

        Runnable resetBtn = () -> {
            if (watchdog[0] != null) { watchdog[0].stop(); watchdog[0] = null; }
            loginBtn.setDisable(false);
            loginBtn.setText("Login");
        };

        Runnable doLogin = () -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            if (username.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Username and password cannot be empty.");
                return;
            }
            errorLabel.setText("");
            loginBtn.setDisable(true);
            loginBtn.setText("Signing in...");

            watchdog[0] = new Timeline(new KeyFrame(Duration.seconds(8), ev -> {
                errorLabel.setText("Server did not respond. Please try again.");
                resetBtn.run();
            }));
            watchdog[0].play();

            Thread t = new Thread(() -> {
                try {
                    String[] auth = apiService.login(username, password);
                    ApiService.setToken(auth[0]);
                    result[0] = auth;

                    String userRole = auth.length > 1 ? auth[1] : "ADMIN";
                    SessionManager.getInstance().login(auth[0], username, userRole);

                    if ("GUIDE".equals(userRole)) {
                        try {
                            org.example.service.GuideService gs = new org.example.service.GuideService();
                            org.example.model.Guide myGuide = gs.getMe();
                            if (myGuide != null && myGuide.getId() != null) {
                                SessionManager.getInstance().setGuideId(myGuide.getId());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    javafx.application.Platform.runLater(() -> {
                        if (watchdog[0] != null) { watchdog[0].stop(); watchdog[0] = null; }
                        slideshow.stop();
                        stage.close();
                    });
                } catch (Exception ex) {
                    javafx.application.Platform.runLater(() -> {
                        String msg = ex.getMessage();
                        errorLabel.setText(msg != null ? msg : "Connection error. Please try again.");
                        resetBtn.run();
                    });
                }
            });
            t.setDaemon(true);
            t.start();
        };

        loginBtn.setOnAction(e -> doLogin.run());
        passwordField.setOnAction(e -> doLogin.run());
        usernameField.setOnAction(e -> passwordField.requestFocus());

        Scene scene = new Scene(root);
        stage.setScene(scene);

        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        stage.setX((screen.getWidth() - 980) / 2);
        stage.setY((screen.getHeight() - 540) / 2);

        stage.showAndWait();
        return result[0];
    }

    private static javafx.scene.layout.AnchorPane buildSlide(String city, String country, String c1, String c2, String imagePath) {
        javafx.scene.layout.AnchorPane slide = new javafx.scene.layout.AnchorPane();
        slide.setStyle("-fx-background-color: linear-gradient(to bottom right, " + c1 + ", " + c2 + ");");

        java.net.URL imgUrl = LoginController.class.getResource(imagePath);
        if (imgUrl != null) {
            javafx.scene.image.ImageView iv = new javafx.scene.image.ImageView(
                    new javafx.scene.image.Image(imgUrl.toString(), 680, 540, false, true));
            iv.setPreserveRatio(false);
            iv.fitWidthProperty().bind(slide.widthProperty());
            iv.fitHeightProperty().bind(slide.heightProperty());
            javafx.scene.layout.AnchorPane.setTopAnchor(iv, 0.0);
            javafx.scene.layout.AnchorPane.setBottomAnchor(iv, 0.0);
            javafx.scene.layout.AnchorPane.setLeftAnchor(iv, 0.0);
            javafx.scene.layout.AnchorPane.setRightAnchor(iv, 0.0);
            slide.getChildren().add(iv);
        }

        javafx.scene.layout.Region bottomGrad = new javafx.scene.layout.Region();
        bottomGrad.setStyle("-fx-background-color: linear-gradient(to top, rgba(0,0,0,0.75) 0%, transparent 45%);");
        javafx.scene.layout.AnchorPane.setTopAnchor(bottomGrad, 0.0);
        javafx.scene.layout.AnchorPane.setBottomAnchor(bottomGrad, 0.0);
        javafx.scene.layout.AnchorPane.setLeftAnchor(bottomGrad, 0.0);
        javafx.scene.layout.AnchorPane.setRightAnchor(bottomGrad, 0.0);
        slide.getChildren().add(bottomGrad);

        VBox info = new VBox(2);
        javafx.scene.layout.AnchorPane.setBottomAnchor(info, 32.0);
        javafx.scene.layout.AnchorPane.setLeftAnchor(info, 20.0);

        Label cityLabel = new Label(city);
        cityLabel.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-weight: bold;");

        Label countryLabel = new Label(country);
        countryLabel.setStyle("-fx-text-fill: #CFD8DC; -fx-font-size: 12px;");

        info.getChildren().addAll(cityLabel, countryLabel);
        slide.getChildren().add(info);
        return slide;
    }
}