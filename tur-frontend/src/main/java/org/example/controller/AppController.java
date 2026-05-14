package org.example.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AppController {

    private static AppController instance;

    @FXML private StackPane contentArea;
    @FXML private SidebarController sidebarController;

    private final Map<String, Node> viewCache = new HashMap<>();
    private final Map<String, Object> controllerCache = new HashMap<>();
    private final Set<String> dirtyViews = new HashSet<>();
    private String currentView;

    public static AppController getInstance() { return instance; }

    @FXML
    public void initialize() {
        instance = this;
        sidebarController.setAppController(this);
    }

    public void setRole(String role) {
        sidebarController.setRole(role);
        boolean isGuide = "GUIDE".equals(role);
        navigateTo(isGuide ? "guideDashboard.fxml" : "dashboard.fxml");
        new Thread(() -> {
            try { Thread.sleep(500); } catch (InterruptedException ignored) {}
            Platform.runLater(() -> {
                if (isGuide) {
                    preload("guideTourManagement.fxml");
                } else {
                    preload("tourManagement.fxml");
                    preload("expenseTracker.fxml");
                    preload("vehiclesGuides.fxml");
                }
            });
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

    public void refreshAllCached() {
        // Refresh only the currently visible view immediately; mark others dirty.
        for (String fxml : controllerCache.keySet()) {
            if (fxml.equals(currentView)) {
                callRefresh(controllerCache.get(fxml));
            } else {
                dirtyViews.add(fxml);
            }
        }
    }

    public void invalidateOtherViews(String currentView) {
        for (String fxml : controllerCache.keySet()) {
            if (!fxml.equals(currentView)) dirtyViews.add(fxml);
        }
    }

    public SidebarController getSidebarController() { return sidebarController; }
}
