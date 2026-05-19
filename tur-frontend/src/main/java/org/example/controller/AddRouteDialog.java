package org.example.controller;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.model.Route;
import org.example.model.Toll;
import org.example.model.Waypoint;

import java.util.ArrayList;
import java.util.List;

public class AddRouteDialog {

    private static HBox draggedRow = null;

    public static Route show(Route existingRoute) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);

        boolean isEdit = (existingRoute != null);
        Label titleLabel = new Label(isEdit ? "Edit Route" : "Add New Route");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1A237E;");

        TextField nameField = new TextField();
        nameField.setPromptText("Route Name");
        TextField startCityField = new TextField();
        startCityField.setPromptText("Start City");
        TextField endCityField = new TextField();
        endCityField.setPromptText("End City");
        TextField countryField = new TextField();
        countryField.setPromptText("Country");
        TextField distanceField = new TextField();
        distanceField.setPromptText("Distance (km)");
        TextField basePriceField = new TextField();
        basePriceField.setPromptText("Base Price (€)");

        if (isEdit) {
            nameField.setText(existingRoute.getRouteName());
            startCityField.setText(existingRoute.getStartCity());
            endCityField.setText(existingRoute.getEndCity());
            if (existingRoute.getCountry() != null) countryField.setText(existingRoute.getCountry());
            if (existingRoute.getDistance() != null) distanceField.setText(String.valueOf(existingRoute.getDistance()));
            if (existingRoute.getBasePrice() != null) basePriceField.setText(String.valueOf(existingRoute.getBasePrice()));
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.addRow(0, new Label("Route Name *:"), nameField, new Label("Country:"), countryField);
        grid.addRow(1, new Label("Start City *:"), startCityField, new Label("Distance:"), distanceField);
        grid.addRow(2, new Label("End City *:"), endCityField, new Label("Base Price:"), basePriceField);

        VBox waypointsContainer = new VBox(8);
        Button addWaypointBtn = new Button("+ Add Waypoint");
        addWaypointBtn.getStyleClass().add("btn-secondary");
        addWaypointBtn.setOnAction(e -> waypointsContainer.getChildren().add(createWaypointRow(null, waypointsContainer)));

        HBox wpHeader = new HBox(15, new Label("Waypoints (Drag ≡ to reorder)"), addWaypointBtn);
        wpHeader.setAlignment(Pos.CENTER_LEFT);
        wpHeader.setStyle("-fx-font-weight: bold; -fx-padding: 10 0 5 0;");

        if (isEdit && existingRoute.getDefaultWaypoints() != null) {
            for (Waypoint wp : existingRoute.getDefaultWaypoints()) {
                waypointsContainer.getChildren().add(createWaypointRow(wp, waypointsContainer));
            }
        }

        VBox tollsContainer = new VBox(8);
        Button addTollBtn = new Button("+ Add Toll");
        addTollBtn.getStyleClass().add("btn-secondary");
        addTollBtn.setOnAction(e -> tollsContainer.getChildren().add(createTollRow(null, tollsContainer)));

        HBox tollHeader = new HBox(15, new Label("Tolls"), addTollBtn);
        tollHeader.setAlignment(Pos.CENTER_LEFT);
        tollHeader.setStyle("-fx-font-weight: bold; -fx-padding: 10 0 5 0;");

        if (isEdit && existingRoute.getTolls() != null) {
            for (Toll t : existingRoute.getTolls()) {
                tollsContainer.getChildren().add(createTollRow(t, tollsContainer));
            }
        }

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        Button saveBtn = new Button("Save");
        saveBtn.getStyleClass().add("btn-primary");
        Button cancelBtn = new Button("Cancel");
        cancelBtn.getStyleClass().add("btn-secondary");

        Route[] result = {null};

        saveBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            String start = startCityField.getText().trim();
            String end = endCityField.getText().trim();

            if (name.isEmpty() || start.isEmpty() || end.isEmpty()) {
                errorLabel.setText("Route Name, Start City, and End City are required!");
                return;
            }

            Float distance = null;
            if (!distanceField.getText().trim().isEmpty()) {
                try { distance = Float.parseFloat(distanceField.getText().trim()); }
                catch (Exception ex) { errorLabel.setText("Invalid Distance."); return; }
            }

            Double basePrice = null;
            if (!basePriceField.getText().trim().isEmpty()) {
                try { basePrice = Double.parseDouble(basePriceField.getText().trim()); }
                catch (Exception ex) { errorLabel.setText("Invalid Base Price."); return; }
            }

            Route r = new Route();
            if (isEdit) r.setRouteId(existingRoute.getRouteId());
            r.setRouteName(name);
            r.setStartCity(start);
            r.setEndCity(end);
            r.setCountry(countryField.getText().trim().isEmpty() ? null : countryField.getText().trim());
            r.setDistance(distance);
            r.setBasePrice(basePrice);

            List<Waypoint> wps = new ArrayList<>();
            for (javafx.scene.Node node : waypointsContainer.getChildren()) {
                if (node instanceof HBox row) {
                    TextField cField = (TextField) row.getChildren().get(1);
                    TextField cyField = (TextField) row.getChildren().get(2);
                    TextField fField = (TextField) row.getChildren().get(3);
                    Waypoint wp = new Waypoint();
                    wp.setCity(cField.getText().trim());
                    wp.setCountry(cyField.getText().trim());
                    try { wp.setExtraFee(fField.getText().trim().isEmpty() ? 0.0 : Double.parseDouble(fField.getText().trim())); }
                    catch (Exception ignored) { wp.setExtraFee(0.0); }
                    wps.add(wp);
                }
            }
            r.setDefaultWaypoints(wps);

            List<Toll> tolls = new ArrayList<>();
            for (javafx.scene.Node node : tollsContainer.getChildren()) {
                if (node instanceof HBox row) {
                    TextField tnField = (TextField) row.getChildren().get(0);
                    TextField tlField = (TextField) row.getChildren().get(1);
                    TextField c1Field = (TextField) row.getChildren().get(2);
                    TextField c2Field = (TextField) row.getChildren().get(3);
                    TextField c3Field = (TextField) row.getChildren().get(4);
                    Toll t = new Toll();
                    t.setName(tnField.getText().trim());
                    t.setLocation(tlField.getText().trim());
                    try { t.setCostCat1(c1Field.getText().trim().isEmpty() ? 0.0f : Float.parseFloat(c1Field.getText().trim())); } catch (Exception ignored) {}
                    try { t.setCostCat2(c2Field.getText().trim().isEmpty() ? 0.0f : Float.parseFloat(c2Field.getText().trim())); } catch (Exception ignored) {}
                    try { t.setCostCat3(c3Field.getText().trim().isEmpty() ? 0.0f : Float.parseFloat(c3Field.getText().trim())); } catch (Exception ignored) {}
                    tolls.add(t);
                }
            }
            r.setTolls(tolls);

            result[0] = r;
            stage.close();
        });

        cancelBtn.setOnAction(e -> stage.close());

        HBox btnBox = new HBox(10, cancelBtn, saveBtn);
        btnBox.setAlignment(Pos.CENTER_RIGHT);

        VBox contentBox = new VBox(15, titleLabel, grid, wpHeader, waypointsContainer, tollHeader, tollsContainer, errorLabel, btnBox);
        contentBox.setPadding(new Insets(25));
        contentBox.setStyle("-fx-background-color: white;");

        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: white; -fx-border-color: #cfd8dc; -fx-border-width: 1px;");
        scrollPane.setPrefSize(800, 600);

        Scene scene = new Scene(scrollPane);
        try { scene.getStylesheets().add(AddRouteDialog.class.getResource("/styles/styles.css").toExternalForm()); } catch (Exception ignored) {}
        stage.setScene(scene);
        stage.showAndWait();

        return result[0];
    }

    private static HBox createWaypointRow(Waypoint wp, VBox container) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 8; -fx-background-radius: 4; -fx-border-color: #e0e0e0; -fx-border-radius: 4;");

        Label dragHandle = new Label("≡");
        dragHandle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #9e9e9e; -fx-cursor: hand;");
        dragHandle.setPadding(new Insets(0, 5, 0, 5));

        TextField cityField = new TextField(wp != null && wp.getCity() != null ? wp.getCity() : "");
        cityField.setPromptText("City");
        HBox.setHgrow(cityField, Priority.ALWAYS);

        TextField countryField = new TextField(wp != null && wp.getCountry() != null ? wp.getCountry() : "");
        countryField.setPromptText("Country");
        HBox.setHgrow(countryField, Priority.ALWAYS);

        TextField feeField = new TextField(wp != null && wp.getExtraFee() != null ? String.valueOf(wp.getExtraFee()) : "");
        feeField.setPromptText("Fee (€)");
        feeField.setPrefWidth(80);

        Button removeBtn = new Button("✕");
        removeBtn.setStyle("-fx-background-color: #ffcdd2; -fx-text-fill: #c62828; -fx-font-weight: bold; -fx-cursor: hand;");
        removeBtn.setOnAction(e -> container.getChildren().remove(row));

        row.getChildren().addAll(dragHandle, cityField, countryField, feeField, removeBtn);

        dragHandle.setOnDragDetected(e -> {
            Dragboard db = dragHandle.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString("wp_row");
            db.setContent(content);
            draggedRow = row;
            e.consume();
        });

        row.setOnDragOver(e -> {
            if (e.getGestureSource() != row && e.getDragboard().hasString()) {
                e.acceptTransferModes(TransferMode.MOVE);
            }
            e.consume();
        });

        row.setOnDragEntered(e -> {
            if (e.getGestureSource() != row && e.getDragboard().hasString()) {
                row.setStyle("-fx-background-color: #e0f7fa; -fx-padding: 8; -fx-background-radius: 4; -fx-border-color: #00bcd4; -fx-border-radius: 4;");
            }
        });

        row.setOnDragExited(e -> row.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 8; -fx-background-radius: 4; -fx-border-color: #e0e0e0; -fx-border-radius: 4;"));

        row.setOnDragDropped(e -> {
            if (draggedRow != null && draggedRow != row) {
                int dropIndex = container.getChildren().indexOf(row);
                container.getChildren().remove(draggedRow);
                container.getChildren().add(dropIndex, draggedRow);
                e.setDropCompleted(true);
            } else {
                e.setDropCompleted(false);
            }
            e.consume();
        });

        return row;
    }

    private static HBox createTollRow(Toll toll, VBox container) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 8; -fx-background-radius: 4; -fx-border-color: #e0e0e0; -fx-border-radius: 4;");

        TextField nameField = new TextField(toll != null && toll.getName() != null ? toll.getName() : "");
        nameField.setPromptText("Toll Name");
        HBox.setHgrow(nameField, Priority.ALWAYS);

        TextField locField = new TextField(toll != null && toll.getLocation() != null ? toll.getLocation() : "");
        locField.setPromptText("Location");
        HBox.setHgrow(locField, Priority.ALWAYS);

        TextField c1Field = new TextField(toll != null && toll.getCostCat1() != null ? String.valueOf(toll.getCostCat1()) : "");
        c1Field.setPromptText("Cat 1 (€)");
        c1Field.setPrefWidth(70);

        TextField c2Field = new TextField(toll != null && toll.getCostCat2() != null ? String.valueOf(toll.getCostCat2()) : "");
        c2Field.setPromptText("Cat 2 (€)");
        c2Field.setPrefWidth(70);

        TextField c3Field = new TextField(toll != null && toll.getCostCat3() != null ? String.valueOf(toll.getCostCat3()) : "");
        c3Field.setPromptText("Cat 3 (€)");
        c3Field.setPrefWidth(70);

        Button removeBtn = new Button("✕");
        removeBtn.setStyle("-fx-background-color: #ffcdd2; -fx-text-fill: #c62828; -fx-font-weight: bold; -fx-cursor: hand;");
        removeBtn.setOnAction(e -> container.getChildren().remove(row));

        row.getChildren().addAll(nameField, locField, c1Field, c2Field, c3Field, removeBtn);

        return row;
    }
}
