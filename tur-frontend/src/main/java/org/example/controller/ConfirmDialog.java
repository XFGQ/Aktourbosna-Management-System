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
        titleLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");

        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-font-size: 13px;");

        Button confirmBtn = new Button("Confirm");
        Button cancelBtn  = new Button("Cancel");
        confirmBtn.getStyleClass().add("btn-danger");
        cancelBtn.getStyleClass().add("btn-secondary");
        confirmBtn.setPrefWidth(90);
        cancelBtn.setPrefWidth(90);

        confirmBtn.setOnAction(e -> { result[0] = true; stage.close(); });
        cancelBtn.setOnAction(e -> stage.close());

        HBox buttons = new HBox(10, cancelBtn, confirmBtn);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        VBox root = new VBox(14, titleLabel, messageLabel, buttons);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: white; -fx-border-color: #BDBDBD; -fx-border-width: 1px;");
        root.setPrefWidth(360);

        stage.setScene(new Scene(root));
        stage.showAndWait();
        return result[0];
    }
}
