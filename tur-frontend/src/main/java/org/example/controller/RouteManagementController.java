package org.example.controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.model.Route;
import org.example.service.RouteService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class RouteManagementController {

    @FXML private Label totalRoutesValue;

    @FXML private TableView<Route> routesTable;
    @FXML private TableColumn<Route, String>  colRouteName;
    @FXML private TableColumn<Route, String>  colStartCity;
    @FXML private TableColumn<Route, String>  colEndCity;
    @FXML private TableColumn<Route, String>  colCountry;
    @FXML private TableColumn<Route, Number>  colDistance;
    @FXML private TableColumn<Route, String>  colBasePrice;
    @FXML private TableColumn<Route, Route>   colActions;

    private final RouteService routeService = new RouteService();

    @FXML
    public void initialize() {
        colRouteName.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getRouteName()));
        colStartCity.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getStartCity()));
        colEndCity.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getEndCity()));
        colCountry.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getCountry() != null ? cd.getValue().getCountry() : "—"));
        colDistance.setCellValueFactory(cd -> new SimpleObjectProperty<>(cd.getValue().getDistance()));
        colBasePrice.setCellValueFactory(cd -> {
            Double p = cd.getValue().getBasePrice();
            return new SimpleStringProperty(p != null ? "€" + String.format("%,.0f", p) : "—");
        });

        colActions.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue()));
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button deleteBtn = new Button("Delete");
            private final HBox box = new HBox(deleteBtn);
            {
                box.setAlignment(Pos.CENTER);
                deleteBtn.getStyleClass().add("btn-danger");
                deleteBtn.setOnAction(e -> onDeleteRoute(getTableView().getItems().get(getIndex())));
            }
            @Override protected void updateItem(Route r, boolean empty) {
                super.updateItem(r, empty);
                setGraphic(empty || r == null ? null : box);
            }
        });

        loadData();
    }

    @FXML
    public void refresh() { loadData(); }

    private void loadData() {
        CompletableFuture<List<Route>> fut = CompletableFuture.supplyAsync(() -> {
            try { return routeService.getAllRoutes(); }
            catch (Exception e) { throw new CompletionException(e); }
        });
        fut.whenComplete((routes, ex) -> javafx.application.Platform.runLater(() -> {
            if (ex != null) {
                ex.printStackTrace();
                totalRoutesValue.setText("—");
            } else {
                routesTable.getItems().setAll(routes);
                totalRoutesValue.setText(String.valueOf(routes.size()));
            }
        }));
    }

    @FXML
    private void onAddRoute() {
        Route newRoute = AddRouteDialog.show();
        if (newRoute == null) return;
        runInBackground(
                () -> routeService.addRoute(newRoute),
                "Adding route...", "Route added successfully.", "Failed to add route");
    }

    private void onDeleteRoute(Route route) {
        Toast.error("Route deletion is not available. Please contact the administrator to remove routes from the backend.");
    }

    private void runInBackground(BackgroundOp op, String loadingMsg, String successMsg, String errorTitle) {
        Stage loadingStage = new Stage();
        loadingStage.initModality(Modality.APPLICATION_MODAL);
        loadingStage.initStyle(StageStyle.UNDECORATED);
        loadingStage.setResizable(false);

        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setPrefSize(40, 40);
        Label msg = new Label(loadingMsg);
        msg.setStyle("-fx-font-size: 14px;");
        VBox box = new VBox(12, spinner, msg);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(24));
        box.setStyle("-fx-background-color: white; -fx-border-color: #BDBDBD; -fx-border-width: 1px;");
        loadingStage.setScene(new javafx.scene.Scene(box));
        loadingStage.show();

        Task<Void> task = new Task<>() {
            @Override protected Void call() throws Exception { op.run(); return null; }
        };
        task.setOnSucceeded(e -> { loadingStage.close(); loadData(); Toast.success(successMsg); });
        task.setOnFailed(e -> {
            loadingStage.close();
            Toast.error(errorTitle + ": " + task.getException().getMessage());
        });
        new Thread(task).start();
    }

    @FunctionalInterface
    private interface BackgroundOp { void run() throws Exception; }
}
