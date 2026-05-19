package org.example.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import org.example.service.GuideService;
import org.example.service.RouteService;
import org.example.service.TourService;
import org.example.service.VehicleService;

import java.util.HashMap;
import java.util.Map;

public class AppController {

    private static AppController instance;

    @FXML private StackPane contentArea;
    @FXML private SidebarController sidebarController;

    private final Map<String, Node>   viewCache       = new HashMap<>();
    private final Map<String, Object> controllerCache = new HashMap<>();

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
        // Preload other views immediately; their data loads are async so no UI blocking
        if (isGuide) {
            preload("guideTourManagement.fxml");
        } else {
            preload("tourManagement.fxml");
            preload("expenseTracker.fxml");
            preload("vehiclesGuides.fxml");
            preload("routeManagement.fxml");
        }
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
            contentArea.getChildren().setAll(view);
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

    /**
     * Called after any CRUD operation. Immediately refreshes ALL cached views
     * (except the one that triggered it — it refreshes itself). With the
     * service-level cache, all concurrent getAllTours/getAll* calls share one
     * HTTP round-trip, so this is cheap.
     */
    public void invalidateOtherViews(String changedBy) {
        for (Map.Entry<String, Object> entry : controllerCache.entrySet()) {
            String fxml = entry.getKey();
            if (!fxml.equals(changedBy)) {
                Object ctrl = entry.getValue();
                Platform.runLater(() -> callRefresh(ctrl));
            }
        }
    }

    /** Triggered by the Refresh button — clears all service caches and reloads every view. */
    public void refreshAllCached() {
        TourService.invalidateCache();
        VehicleService.invalidateCache();
        GuideService.invalidateCache();
        RouteService.invalidateCache();
        for (Object ctrl : controllerCache.values()) {
            Platform.runLater(() -> callRefresh(ctrl));
        }
    }

    public SidebarController getSidebarController() { return sidebarController; }
}
