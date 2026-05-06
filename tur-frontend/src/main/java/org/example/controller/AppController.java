package org.example.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AppController {

    private static AppController instance;

    @FXML private StackPane contentArea;
    @FXML private SidebarController sidebarController;

    private final Map<String, Node> viewCache = new HashMap<>();
    private final Map<String, Object> controllerCache = new HashMap<>();
    private final Set<String> dirtyViews = new java.util.HashSet<>();

    public static AppController getInstance() {
        return instance;
    }

    @FXML
    public void initialize() {
        instance = this;
        sidebarController.setAppController(this);
        navigateTo("dashboard.fxml");

        // Diğer ekranları 1.5 saniye sonra arka planda yükle
        // Böylece Dashboard önce görünür
        new Thread(() -> {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException ignored) {}
            Platform.runLater(() -> {
                preload("tourManagement.fxml");
                preload("expenseTracker.fxml");
                preload("vehiclesGuides.fxml");
            });
        }).start();
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
            Object controller = controllerCache.get(fxmlFile);

            if (view == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxmlFile));
                view = loader.load();
                controller = loader.getController();
                viewCache.put(fxmlFile, view);
                controllerCache.put(fxmlFile, controller);
                contentArea.getChildren().setAll(view);
            } else if (dirtyViews.contains(fxmlFile)) {
                // Ekran kirli - loading göster, arka planda refresh, sonra göster
                dirtyViews.remove(fxmlFile);
                final Node finalView = view;
                final Object finalController = controller;
                showLoadingAndRefresh(finalController, () -> contentArea.getChildren().setAll(finalView));
            } else {
                // Cache temiz, anlık geçiş
                contentArea.getChildren().setAll(view);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showLoadingAndRefresh(Object controller, Runnable onDone) {
        Stage loadingStage = new Stage();
        loadingStage.initModality(Modality.APPLICATION_MODAL);
        loadingStage.initStyle(StageStyle.UNDECORATED);
        loadingStage.setResizable(false);

        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setPrefSize(36, 36);
        Label msg = new Label("Loading...");
        msg.setStyle("-fx-font-size: 13px;");
        VBox vbox = new VBox(10, spinner, msg);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: white; -fx-border-color: #BDBDBD; -fx-border-width: 1px;");

        loadingStage.setScene(new javafx.scene.Scene(vbox));
        loadingStage.show();

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                refreshController(controller);
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            loadingStage.close();
            onDone.run();
        });

        task.setOnFailed(e -> {
            loadingStage.close();
            onDone.run();
        });

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private void refreshController(Object controller) {
        if (controller == null) return;
        try {
            controller.getClass().getDeclaredMethod("refresh").invoke(controller);
        } catch (NoSuchMethodException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Diğer ekranların cache'ini "kirli" işaretle */
    public void invalidateOtherViews(String currentView) {
        for (String fxml : controllerCache.keySet()) {
            if (!fxml.equals(currentView)) {
                dirtyViews.add(fxml);
            }
        }
    }

    /** Tüm ekranları zorla refresh et */
    public void refreshAll() {
        for (Object controller : controllerCache.values()) {
            refreshController(controller);
        }
    }

    public SidebarController getSidebarController() {
        return sidebarController;
    }
}