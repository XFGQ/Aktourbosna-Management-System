package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

public class AppController {

    @FXML private StackPane contentArea;
    @FXML private SidebarController sidebarController;

    @FXML
    public void initialize() {
        sidebarController.setAppController(this);
        navigateTo("dashboard.fxml");
    }

    public void navigateTo(String fxmlFile) {
        try {
            Node view = FXMLLoader.load(getClass().getResource("/fxml/" + fxmlFile));
            contentArea.getChildren().setAll(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
