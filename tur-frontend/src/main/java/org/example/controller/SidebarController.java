package org.example.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.example.service.SessionManager;

import java.awt.Desktop;
import java.net.URI;

public class SidebarController {

    @FXML private Button btnDashboard;
    @FXML private Button btnTourManagement;
    @FXML private Button btnExpenseTracker;
    @FXML private Button btnVehiclesGuides;
    @FXML private Button btnRoutes;
    @FXML private Button btnRefresh;
    @FXML private Button btnSupport;
    @FXML private HBox logoHBox;

    private AppController appController;
    private Runnable onLogout;

    @FXML
    public void initialize() {
        if (logoHBox != null) {
            ImageView logo = LogoView.create(46);
            logoHBox.getChildren().add(0, logo);
        }
        if (SessionManager.getInstance().isGuide()) {
            if (btnVehiclesGuides != null) btnVehiclesGuides.setText("  Vehicles");
        }
    }

    public void setAppController(AppController appController) {
        this.appController = appController;
        setActive(btnDashboard);
    }

    public void setRole(String role) {
    }

    public void setOnLogout(Runnable onLogout) {
        this.onLogout = onLogout;
    }

    @FXML
    private void onDashboard() {
        appController.navigateTo(SessionManager.getInstance().isGuide() ? "guideDashboard.fxml" : "dashboard.fxml");
        setActive(btnDashboard);
    }

    @FXML
    private void onTourManagement() {
        appController.navigateTo(SessionManager.getInstance().isGuide() ? "guideTourManagement.fxml" : "tourManagement.fxml");
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
    private void onRoutes() {
        appController.navigateTo("routes.fxml");
        setActive(btnRoutes);
    }

    @FXML
    private void onRefresh() {
        if (appController != null) appController.refreshAllCached();
    }

    @FXML
    private void onSupport() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Support & Contact");
        alert.setHeaderText("Need help or found an error?");
        alert.setContentText("You can contact us using the following emails:\n\n" +
                "• Company Owner: info@aktourbosna.com\n" +
                "• Developer: info@rinnesoft.com");

        ButtonType openMailBtn = new ButtonType("Send Email");
        ButtonType closeBtn = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(openMailBtn, closeBtn);

        alert.showAndWait().ifPresent(response -> {
            if (response == openMailBtn) {
                try {
                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.MAIL)) {
                        String mailto = "mailto:info@aktourbosna.com,info@rinnesoft.com?subject=Aktour%20ViaBalkan%20Support%20Request";
                        Desktop.getDesktop().mail(new URI(mailto));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    private void onLogout() {
        SessionManager.getInstance().logout();
        if (onLogout != null) onLogout.run();
        else { Platform.exit(); System.exit(0); }
    }

    private void setActive(Button active) {
        for (Button btn : new Button[]{btnDashboard, btnTourManagement, btnExpenseTracker, btnVehiclesGuides, btnRoutes}) {
            if (btn != null) {
                btn.getStyleClass().remove("nav-button-active");
                if (!btn.getStyleClass().contains("nav-button")) {
                    btn.getStyleClass().add("nav-button");
                }
            }
        }
        if (active != null) {
            active.getStyleClass().remove("nav-button");
            if (!active.getStyleClass().contains("nav-button-active")) {
                active.getStyleClass().add("nav-button-active");
            }
        }
    }
}