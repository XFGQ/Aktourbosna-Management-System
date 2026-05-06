package org.example;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.controller.LoginController;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Stage splash = createSplashScreen();
        splash.show();

        ProgressBar bar = (ProgressBar) splash.getScene().getRoot().lookup("#splashProgress");
        Label status = (Label) splash.getScene().getRoot().lookup("#splashStatus");

        Platform.runLater(() -> {
            javafx.scene.layout.Region track = (javafx.scene.layout.Region) bar.lookup(".track");
            if (track != null)
                track.setStyle("-fx-background-color: rgba(255,255,255,0.15); -fx-background-radius: 5px;");
            javafx.scene.layout.Region fill = (javafx.scene.layout.Region) bar.lookup(".bar");
            if (fill != null)
                fill.setStyle("-fx-background-color: #4FC3F7; -fx-background-radius: 5px; -fx-background-insets: 0; -fx-padding: 0;");
        });

        // Bar'ı sürekli yumuşak şekilde dolduran animator
        ProgressAnimator animator = new ProgressAnimator(bar);
        animator.start();

        // Sadece animasyon - FXML yükleme FX thread'de yapılacak
        Task<Void> loadTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Platform.runLater(() -> status.setText("Loading components..."));
                animator.target = 0.30;
                Thread.sleep(200);

                Platform.runLater(() -> status.setText("Building UI..."));
                animator.target = 0.60;
                Thread.sleep(200);

                Platform.runLater(() -> status.setText("Almost ready..."));
                animator.target = 0.90;
                Thread.sleep(150);

                return null;
            }
        };

        loadTask.setOnSucceeded(e -> {
            animator.target = 1.0;
            status.setText("Ready!");
            Task<Void> finishTask = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    Thread.sleep(250);
                    return null;
                }
            };
            finishTask.setOnSucceeded(ev -> {
                try {
                    animator.stop();
                    splash.close();

                    String[] auth = LoginController.showLoginScreen();
                    if (auth == null) {
                        Platform.exit();
                        return;
                    }

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
                    Parent root = loader.load();
                    primaryStage.setTitle("Aktour ViaBalkan Management System");
                    primaryStage.setScene(new Scene(root, 1200, 800));
                    primaryStage.show();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Platform.exit();
                }
            });
            new Thread(finishTask).start();
        });

        loadTask.setOnFailed(e -> {
            animator.stop();
            loadTask.getException().printStackTrace();
            splash.close();
        });

        Thread t = new Thread(loadTask);
        t.setDaemon(true);
        t.start();
    }

    private Stage createSplashScreen() {
        Stage splash = new Stage();
        splash.initStyle(StageStyle.UNDECORATED);

        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #1A237E, #3949AB);");

        Label logoText = new Label("Aktour ViaBalkan");
        logoText.setStyle("-fx-text-fill: white; -fx-font-size: 32px; -fx-font-weight: bold;");

        Label subtitle = new Label("Management System");
        subtitle.setStyle("-fx-text-fill: #B0BEC5; -fx-font-size: 14px;");

        VBox spacer = new VBox();
        spacer.setPrefHeight(30);

        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setId("splashProgress");
        progressBar.setPrefWidth(280);
        progressBar.setPrefHeight(10);
        progressBar.setStyle("-fx-accent: #4FC3F7;");

        Label status = new Label("Initializing...");
        status.setId("splashStatus");
        status.setStyle("-fx-text-fill: #B0BEC5; -fx-font-size: 11px;");

        Label version = new Label("v1.0");
        version.setStyle("-fx-text-fill: #757575; -fx-font-size: 10px;");

        root.getChildren().addAll(logoText, subtitle, spacer, progressBar, status, version);

        Scene scene = new Scene(root, 480, 320);
        splash.setScene(scene);

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        splash.setX((screenBounds.getWidth() - 480) / 2);
        splash.setY((screenBounds.getHeight() - 320) / 2);

        return splash;
    }

    /**
     * Bar'ı yumuşak şekilde target değerine doğru iten animator.
     * 60fps, her frame'de 1-2% ilerler.
     */
    private static class ProgressAnimator extends AnimationTimer {
        private final ProgressBar bar;
        volatile double target = 0.0;

        ProgressAnimator(ProgressBar bar) {
            this.bar = bar;
        }

        @Override
        public void handle(long now) {
            double current = bar.getProgress();
            if (current < target) {
                double next = current + (target - current) * 0.05;
                if (next > target) next = target;
                bar.setProgress(next);
            }
        }
    }
}