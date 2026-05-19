package org.example.controller;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.model.Route;

import java.util.ArrayList;

public class AddRouteDialog {

    public static Route show(Route existingRoute) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);

        boolean isEdit = (existingRoute != null);
        Label titleLabel = new Label(isEdit ? "Edit Route" : "Add New Route");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1A237E;");

        TextField nameField = new TextField();
        nameField.setPromptText("Route Name (e.g. Balkan Express)");

        TextField startCityField = new TextField();
        startCityField.setPromptText("Start City");

        TextField endCityField = new TextField();
        endCityField.setPromptText("End City");

        TextField countryField = new TextField();
        countryField.setPromptText("Country (Optional)");

        TextField distanceField = new TextField();
        distanceField.setPromptText("Distance in km (Optional)");

        TextField basePriceField = new TextField();
        basePriceField.setPromptText("Base Price in € (Optional)");

        // Eğer düzenleme modundaysak mevcut verileri kutulara doldur
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
        grid.setVgap(15);
        grid.addRow(0, new Label("Route Name *:"), nameField);
        grid.addRow(1, new Label("Start City *:"), startCityField);
        grid.addRow(2, new Label("End City *:"), endCityField);
        grid.addRow(3, new Label("Country:"), countryField);
        grid.addRow(4, new Label("Distance (km):"), distanceField);
        grid.addRow(5, new Label("Base Price (€):"), basePriceField);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 11px;");

        Button saveBtn = new Button("Save");
        saveBtn.getStyleClass().add("btn-primary");
        Button cancelBtn = new Button("Cancel");
        cancelBtn.getStyleClass().add("btn-secondary");

        Route[] result = {null};

        saveBtn.setOnAction(e -> {
            try {
                String name = nameField.getText().trim();
                String start = startCityField.getText().trim();
                String end = endCityField.getText().trim();

                // VERİTABANI KONTROLÜ: NOT NULL olan alanlar boş bırakılamaz!
                if (name.isEmpty() || start.isEmpty() || end.isEmpty()) {
                    errorLabel.setText("Route Name, Start City and End City are required!");
                    return;
                }

                Float distance = null;
                if (!distanceField.getText().trim().isEmpty()) {
                    distance = Float.parseFloat(distanceField.getText().trim());
                }

                Double basePrice = null;
                if (!basePriceField.getText().trim().isEmpty()) {
                    basePrice = Double.parseDouble(basePriceField.getText().trim());
                }

                Route r = new Route();
                if (isEdit) r.setRouteId(existingRoute.getRouteId());
                r.setRouteName(name);
                r.setStartCity(start);
                r.setEndCity(end);

                String country = countryField.getText().trim();
                r.setCountry(country.isEmpty() ? null : country);

                r.setDistance(distance);
                r.setBasePrice(basePrice);

                // Modelin içindeki listeleri (Waypoints ve Tolls) NullPointerException yememek için boş liste olarak başlatıyoruz
                if (isEdit && existingRoute.getDefaultWaypoints() != null) {
                    r.setDefaultWaypoints(existingRoute.getDefaultWaypoints());
                    r.setTolls(existingRoute.getTolls());
                } else {
                    r.setDefaultWaypoints(new ArrayList<>());
                    r.setTolls(new ArrayList<>());
                }

                result[0] = r;
                stage.close();
            } catch (NumberFormatException ex) {
                errorLabel.setText("Invalid number format for Distance or Price.");
            }
        });

        cancelBtn.setOnAction(e -> stage.close());

        HBox btnBox = new HBox(10, cancelBtn, saveBtn);
        btnBox.setAlignment(Pos.CENTER_RIGHT);

        VBox root = new VBox(20, titleLabel, grid, errorLabel, btnBox);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: white; -fx-border-color: #cfd8dc; -fx-border-width: 1px;");

        Scene scene = new Scene(root);
        try {
            scene.getStylesheets().add(AddRouteDialog.class.getResource("/styles/styles.css").toExternalForm());
        } catch (Exception ignored) {}

        stage.setScene(scene);
        stage.showAndWait();

        return result[0];
    }
}