package org.example.controller;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.animation.ParallelTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class Toast {

    public static void success(String message) {
        show("✓", message, "#1B5E20", "#E8F5E9", "#2E7D32");
    }

    public static void error(String message) {
        show("✕", message, "#B71C1C", "#FFEBEE", "#C62828");
    }

    public static void info(String message) {
        show("ℹ", message, "#0D47A1", "#E3F2FD", "#1565C0");
    }

    private static void show(String icon, String message, String textColor, String bgColor, String borderColor) {
        Platform.runLater(() -> {
            Label iconLabel = new Label(icon);
            iconLabel.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-color: " + borderColor + ";" +
                "-fx-background-radius: 50;" +
                "-fx-min-width: 26px;" +
                "-fx-min-height: 26px;" +
                "-fx-max-width: 26px;" +
                "-fx-max-height: 26px;" +
                "-fx-alignment: center;"
            );

            Label msgLabel = new Label(message);
            msgLabel.setStyle(
                "-fx-text-fill: " + textColor + ";" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;"
            );
            msgLabel.setMaxWidth(320);
            msgLabel.setWrapText(true);

            HBox box = new HBox(10, iconLabel, msgLabel);
            box.setAlignment(Pos.CENTER_LEFT);
            box.setPadding(new Insets(12, 18, 12, 14));
            box.setStyle(
                "-fx-background-color: " + bgColor + ";" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: " + borderColor + ";" +
                "-fx-border-radius: 10;" +
                "-fx-border-width: 1.5;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.20), 12, 0, 0, 4);"
            );

            Stage stage = new Stage(StageStyle.TRANSPARENT);
            stage.setAlwaysOnTop(true);

            Scene scene = new Scene(box);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            stage.show();

            Rectangle2D screen = Screen.getPrimary().getVisualBounds();
            stage.setX(screen.getWidth() - stage.getWidth() - 24);
            stage.setY(screen.getHeight() - stage.getHeight() - 50);

            // Slide up + fade in
            TranslateTransition slide = new TranslateTransition(Duration.millis(250), box);
            slide.setFromY(15);
            slide.setToY(0);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(250), box);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);

            new ParallelTransition(slide, fadeIn).play();

            // Fade out after 3s
            FadeTransition fadeOut = new FadeTransition(Duration.millis(400), box);
            fadeOut.setDelay(Duration.seconds(3));
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(e -> stage.close());
            fadeOut.play();
        });
    }
}
