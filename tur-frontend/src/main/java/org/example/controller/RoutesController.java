package org.example.controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import org.example.service.SessionManager;

public class RoutesController {

    @FXML private Label totalRoutesValue;
    @FXML private Button addRouteBtn;

    @FXML private TableView<Object> routesTable;
    @FXML private TableColumn<Object, String> colRouteId;
    @FXML private TableColumn<Object, String> colRouteName;
    @FXML private TableColumn<Object, String> colRouteStart;
    @FXML private TableColumn<Object, String> colRouteEnd;
    @FXML private TableColumn<Object, String> colRouteDistance;
    @FXML private TableColumn<Object, Object> colRouteActions;

    @FXML
    public void initialize() {
        if (SessionManager.getInstance().isGuide()) {
            if (addRouteBtn != null) {
                addRouteBtn.setVisible(false);
                addRouteBtn.setManaged(false);
            }
            if (colRouteActions != null) {
                colRouteActions.setVisible(false);
            }
        }

        colRouteActions.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue()));
        colRouteActions.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox box = new HBox(6, editBtn, deleteBtn);
            {
                box.setAlignment(Pos.CENTER);
                editBtn.getStyleClass().add("btn-secondary");
                deleteBtn.getStyleClass().add("btn-danger");
                editBtn.setOnAction(e -> onEditRoute(getItem()));
                deleteBtn.setOnAction(e -> onDeleteRoute(getItem()));
            }
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty || item == null ? null : box);
            }
        });

        loadData();
    }

    @FXML
    public void refresh() {
        loadData();
    }

    private void loadData() {
        totalRoutesValue.setText("0");
    }

    @FXML
    private void onAddRoute() {
    }

    private void onEditRoute(Object route) {
    }

    private void onDeleteRoute(Object route) {
    }
}