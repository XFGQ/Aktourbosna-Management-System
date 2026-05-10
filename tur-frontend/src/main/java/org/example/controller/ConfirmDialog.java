package org.example.controller;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ConfirmDialog {

    public static boolean show(String title, String message) {
        boolean[] result = {false};

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setResizable(false);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #1A237E;");

        Label msgLabel = new Label(message);
        msgLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #455A64;");
        msgLabel.setWrapText(true);
        msgLabel.setMaxWidth(320);

        Button confirmBtn = new Button("Delete");
        confirmBtn.setStyle(
            "-fx-background-color: #C62828; -fx-text-fill: white; -fx-font-weight: bold;" +
            "-fx-background-radius: 6; -fx-padding: 8 20; -fx-font-size: 13px; -fx-cursor: hand;"
        );
        confirmBtn.setOnAction(e -> { result[0] = true; stage.close(); });

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle(
            "-fx-background-color: #ECEFF1; -fx-text-fill: #37474F; -fx-font-weight: bold;" +
            "-fx-background-radius: 6; -fx-padding: 8 20; -fx-font-size: 13px; -fx-cursor: hand;"
        );
        cancelBtn.setOnAction(e -> stage.close());

        HBox buttons = new HBox(10, cancelBtn, confirmBtn);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        VBox root = new VBox(14, titleLabel, msgLabel, buttons);
        root.setPadding(new Insets(28, 28, 22, 28));
        root.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #E0E0E0;" +
            "-fx-border-radius: 10;" +
            "-fx-border-width: 1;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 16, 0, 0, 4);"
        );
        root.setPrefWidth(380);

        Scene scene = new Scene(root);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        stage.setScene(scene);
        stage.showAndWait();

        return result[0];
    }
}
