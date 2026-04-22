package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class SidebarController {

    @FXML private Button btnDashboard;
    @FXML private Button btnTourManagement;
    @FXML private Button btnExpenseTracker;
    @FXML private Button btnVehiclesGuides;

    private AppController appController;

    public void setAppController(AppController appController) {
        this.appController = appController;
        setActive(btnDashboard);
    }

    @FXML
    private void onDashboard() {
        appController.navigateTo("dashboard.fxml");
        setActive(btnDashboard);
    }

    @FXML
    private void onTourManagement() {
        appController.navigateTo("tourManagement.fxml");
        setActive(btnTourManagement);
    }

    @FXML
    private void onExpenseTracker() {
        // appController.navigateTo("expense-tracker.fxml");
        setActive(btnExpenseTracker);
    }

    @FXML
    private void onVehiclesGuides() {
        // appController.navigateTo("vehicles-guides.fxml");
        setActive(btnVehiclesGuides);
    }

    private void setActive(Button active) {
        for (Button btn : new Button[]{btnDashboard, btnTourManagement, btnExpenseTracker, btnVehiclesGuides}) {
            btn.getStyleClass().remove("nav-button-active");
            if (!btn.getStyleClass().contains("nav-button")) {
                btn.getStyleClass().add("nav-button");
            }
        }
        active.getStyleClass().remove("nav-button");
        if (!active.getStyleClass().contains("nav-button-active")) {
            active.getStyleClass().add("nav-button-active");
        }
    }
}
