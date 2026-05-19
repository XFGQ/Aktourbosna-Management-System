package org.example.controller;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.model.Route;
import org.example.model.Toll;
import org.example.model.Waypoint;
import org.example.service.RouteService;
import org.example.service.SessionManager;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

public class RoutesController {

    @FXML private Label totalRoutesValue;
    @FXML private Button addRouteBtn;

    @FXML private TableView<Route> routesTable;
    @FXML private TableColumn<Route, Long> colRouteId;
    @FXML private TableColumn<Route, String> colRouteName;
    @FXML private TableColumn<Route, String> colRouteStart;
    @FXML private TableColumn<Route, String> colRouteEnd;
    @FXML private TableColumn<Route, String> colRouteWaypoints;
    @FXML private TableColumn<Route, String> colRouteTolls;
    @FXML private TableColumn<Route, Float> colRouteDistance;
    @FXML private TableColumn<Route, Route> colRouteActions;

    private final RouteService routeService = new RouteService();

    @FXML
    public void initialize() {
        boolean isGuide = SessionManager.getInstance().isGuide();

        if (isGuide) {
            if (addRouteBtn != null) {
                addRouteBtn.setVisible(false);
                addRouteBtn.setManaged(false);
            }
            if (colRouteActions != null) {
                colRouteActions.setVisible(false);
            }
        }

        colRouteId.setCellValueFactory(new PropertyValueFactory<>("routeId"));
        colRouteName.setCellValueFactory(new PropertyValueFactory<>("routeName"));
        colRouteStart.setCellValueFactory(new PropertyValueFactory<>("startCity"));
        colRouteEnd.setCellValueFactory(new PropertyValueFactory<>("endCity"));
        colRouteWaypoints.setCellValueFactory(cd -> {
            List<Waypoint> waypoints = cd.getValue().getDefaultWaypoints();
            if (waypoints == null || waypoints.isEmpty()) {
                return new ReadOnlyObjectWrapper<>("-");
            }
            String wpsString = waypoints.stream()
                    .map(Waypoint::getCity)
                    .collect(Collectors.joining(", "));
            return new ReadOnlyObjectWrapper<>(wpsString);
        });

        colRouteTolls.setCellValueFactory(cd -> {
            List<Toll> tolls = cd.getValue().getTolls();
            if (tolls == null || tolls.isEmpty()) {
                return new ReadOnlyObjectWrapper<>("-");
            }
            String tollsString = tolls.stream()
                    .map(t -> (t.getName() != null ? t.getName() : "") + 
                              (t.getLocation() != null && !t.getLocation().isEmpty() ? ", " + t.getLocation() : ""))
                    .collect(Collectors.joining(" | "));
            return new ReadOnlyObjectWrapper<>(tollsString);
        });

        colRouteDistance.setCellValueFactory(new PropertyValueFactory<>("distance"));

        if (!isGuide) {
            colRouteActions.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue()));
            colRouteActions.setCellFactory(col -> new TableCell<>() {
                private final Button editBtn = new Button("Edit");
                private final Button deleteBtn = new Button("Delete");
                private final HBox box = new HBox(6, editBtn, deleteBtn);
                {
                    box.setAlignment(Pos.CENTER);
                    editBtn.getStyleClass().add("btn-secondary");
                    deleteBtn.getStyleClass().add("btn-danger");
                    editBtn.setOnAction(e -> onEditRoute(getTableView().getItems().get(getIndex())));
                    deleteBtn.setOnAction(e -> onDeleteRoute(getTableView().getItems().get(getIndex())));
                }
                @Override
                protected void updateItem(Route item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty || item == null ? null : box);
                }
            });
        }

        loadData();
    }

    @FXML
    public void refresh() {
        loadData();
    }

    private void loadData() {
        CompletableFuture.supplyAsync(() -> {
            try {
                return routeService.getAllRoutes();
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }).whenComplete((routes, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
                Platform.runLater(() -> totalRoutesValue.setText("—"));
                return;
            }
            Platform.runLater(() -> {
                routesTable.getItems().setAll(routes);
                totalRoutesValue.setText(String.valueOf(routes.size()));
            });
        });
    }

    @FXML
    private void onAddRoute() {
        Route newRoute = AddRouteDialog.show(null);
        if (newRoute == null) return;
        runInBackground(
                () -> routeService.addRoute(newRoute),
                "Adding route...",
                "Route added successfully.",
                "Failed to add route"
        );
    }

    private void onEditRoute(Route route) {
        if (route == null) return;
        Route updated = AddRouteDialog.show(route);
        if (updated == null) return;
        runInBackground(
                () -> routeService.updateRoute(route.getRouteId(), updated),
                "Updating route...",
                "Route updated successfully.",
                "Failed to update route"
        );
    }

    private void onDeleteRoute(Route route) {
        if (!ConfirmDialog.show("Confirm deletion", "Delete route \"" + route.getRouteName() + "\"?")) return;
        runInBackground(
                () -> routeService.deleteRoute(route.getRouteId()),
                "Deleting route...",
                "Route deleted.",
                "Failed to delete route"
        );
    }

    private void runInBackground(BackgroundOp op, String loadingMsg, String successMsg, String errorTitle) {
        Stage loadingStage = new Stage(StageStyle.UNDECORATED);
        loadingStage.initModality(Modality.APPLICATION_MODAL);
        VBox box = new VBox(12, new ProgressIndicator(), new Label(loadingMsg));
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(24));
        box.setStyle("-fx-background-color: white; -fx-border-color: #BDBDBD; -fx-border-width: 1px;");
        loadingStage.setScene(new Scene(box));
        loadingStage.show();

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                op.run();
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            loadingStage.close();
            loadData();
            Toast.success(successMsg);
        });

        task.setOnFailed(e -> {
            loadingStage.close();
            Toast.error(errorTitle + ": " + task.getException().getMessage());
        });

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    @FunctionalInterface
    private interface BackgroundOp {
        void run() throws Exception;
    }
}