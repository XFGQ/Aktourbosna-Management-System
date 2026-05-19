package org.example.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import org.example.model.Guide;
import org.example.service.GuideService;
import org.example.service.RouteService;
import org.example.service.TourService;
import org.example.service.VehicleService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AppController {

    private static AppController instance;

    @FXML private StackPane contentArea;
    @FXML private SidebarController sidebarController;

    private final Map<String, Node>   viewCache       = new HashMap<>();
    private final Map<String, Object> controllerCache = new HashMap<>();
    private final Set<String>         dirtyViews      = new HashSet<>();
    private String currentView;

    public static AppController getInstance() { return instance; }

    @FXML
    public void initialize() {
        instance = this;
        sidebarController.setAppController(this);
    }

    public void setRole(String role) {
        sidebarController.setRole(role);
        if ("GUIDE".equals(role)) {
            checkGuideProfileThenNavigate();
        } else {
            navigateTo("dashboard.fxml");
            new Thread(() -> {
                try { Thread.sleep(500); } catch (InterruptedException ignored) {}
                Platform.runLater(() -> {
                    preload("tourManagement.fxml");
                    preload("expenseTracker.fxml");
                    preload("vehiclesGuides.fxml");
                    preload("routeManagement.fxml");
                });
            }).start();
        }
    }

    private void checkGuideProfileThenNavigate() {
        new Thread(() -> {
            try {
                Guide profile = new GuideService().getMyProfile();
                boolean incomplete = profile.getPhone() == null || profile.getPhone().isBlank()
                        || profile.getBaseCity() == null || profile.getBaseCity().isBlank();
                Platform.runLater(() -> {
                    if (incomplete) {
                        GuideProfileSetupDialog.show(profile);
                        // If ESC pressed, continue to dashboard anyway — dialog re-appears on next login
                    }
                    navigateTo("guideDashboard.fxml");
                    new Thread(() -> {
                        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
                        Platform.runLater(() -> preload("guideTourManagement.fxml"));
                    }).start();
                });
            } catch (Exception e) {
                System.err.println("[GuideProfile] getMyProfile failed: " + e.getMessage());
                Platform.runLater(() -> navigateTo("guideDashboard.fxml"));
            }
        }).start();
    }

    public void setOnLogout(Runnable onLogout) {
        sidebarController.setOnLogout(onLogout);
    }

    private void preload(String fxmlFile) {
        try {
            if (!viewCache.containsKey(fxmlFile)) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxmlFile));
                Node view = loader.load();
                viewCache.put(fxmlFile, view);
                controllerCache.put(fxmlFile, loader.getController());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void navigateTo(String fxmlFile) {
        try {
            Node view = viewCache.get(fxmlFile);
            if (view == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxmlFile));
                view = loader.load();
                viewCache.put(fxmlFile, view);
                controllerCache.put(fxmlFile, loader.getController());
            }
            currentView = fxmlFile;
            contentArea.getChildren().setAll(view);
            if (dirtyViews.remove(fxmlFile)) {
                callRefresh(controllerCache.get(fxmlFile));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callRefresh(Object controller) {
        if (controller == null) return;
        try {
            controller.getClass().getDeclaredMethod("refresh").invoke(controller);
        } catch (NoSuchMethodException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void invalidateOtherViews(String changedBy) {
        for (String fxml : controllerCache.keySet()) {
            if (!fxml.equals(changedBy)) dirtyViews.add(fxml);
        }
    }

    public void refreshAllCached() {
        TourService.invalidateCache();
        VehicleService.invalidateCache();
        GuideService.invalidateCache();
        RouteService.invalidateCache();
        for (String fxml : controllerCache.keySet()) {
            if (fxml.equals(currentView)) {
                callRefresh(controllerCache.get(fxml));
            } else {
                dirtyViews.add(fxml);
            }
        }
    }

    public SidebarController getSidebarController() { return sidebarController; }
}
