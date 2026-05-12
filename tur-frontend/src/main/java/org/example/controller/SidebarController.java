package org.example.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class SidebarController {

    @FXML private Button btnDashboard;
    @FXML private Button btnTourManagement;
    @FXML private Button btnExpenseTracker;
    @FXML private Button btnVehiclesGuides;
    @FXML private HBox logoHBox;

    private AppController appController;
    private String role = "ADMIN";
    private Runnable onLogout;

    @FXML
    public void initialize() {
        if (logoHBox != null) {
            ImageView logo = LogoView.create(46);
            logoHBox.getChildren().add(0, logo);
        }
    }

    @FXML
    public void initialize() {
        if (logoHBox != null) {
            ImageView logo = LogoView.create(46);
            logoHBox.getChildren().add(0, logo);
        }
    }

    public void setAppController(AppController appController) {
        this.appController = appController;
        setActive(btnDashboard);
    }

    public void setRole(String role) {
        this.role = role;
        if ("GUIDE".equals(role)) {
            btnExpenseTracker.setVisible(false);
            btnExpenseTracker.setManaged(false);
            btnVehiclesGuides.setVisible(false);
            btnVehiclesGuides.setManaged(false);
        }
    }

    public void setOnLogout(Runnable onLogout) {
        this.onLogout = onLogout;
    }

    @FXML
    private void onDashboard() {
        appController.navigateTo("GUIDE".equals(role) ? "guideDashboard.fxml" : "dashboard.fxml");
        setActive(btnDashboard);
    }

    @FXML
    private void onTourManagement() {
        appController.navigateTo("GUIDE".equals(role) ? "guideTourManagement.fxml" : "tourManagement.fxml");
        setActive(btnTourManagement);
    }

    @FXML
    private void onExpenseTracker() {
        appController.navigateTo("expenseTracker.fxml");
        setActive(btnExpenseTracker);
    }

    @FXML
    private void onVehiclesGuides() {
        appController.navigateTo("vehiclesGuides.fxml");
        setActive(btnVehiclesGuides);
    }

    @FXML
    private void onLogout() {
        if (onLogout != null) onLogout.run();
        else { Platform.exit(); System.exit(0); }
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
