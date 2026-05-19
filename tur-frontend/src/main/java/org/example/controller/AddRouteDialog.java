package org.example.controller;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import org.example.model.Route;

public class AddRouteDialog {

    public static Route show() {
        Dialog<Route> dialog = new Dialog<>();
        dialog.setTitle("Add Route");
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.UTILITY);

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 28, 10, 28));

        TextField routeNameField = new TextField();
        routeNameField.setPromptText("e.g. Sarajevo - Mostar");
        routeNameField.setPrefWidth(260);

        TextField startCityField = new TextField();
        startCityField.setPromptText("e.g. Sarajevo");
        startCityField.setPrefWidth(260);

        TextField endCityField = new TextField();
        endCityField.setPromptText("e.g. Mostar");
        endCityField.setPrefWidth(260);

        TextField countryField = new TextField();
        countryField.setPromptText("e.g. Bosnia");
        countryField.setPrefWidth(260);

        TextField distanceField = new TextField();
        distanceField.setPromptText("km, e.g. 130");
        distanceField.setPrefWidth(260);

        TextField basePriceField = new TextField();
        basePriceField.setPromptText("€, e.g. 250");
        basePriceField.setPrefWidth(260);

        grid.addRow(0, new Label("Route Name *"),  routeNameField);
        grid.addRow(1, new Label("Start City *"),  startCityField);
        grid.addRow(2, new Label("End City *"),    endCityField);
        grid.addRow(3, new Label("Country"),       countryField);
        grid.addRow(4, new Label("Distance (km)"), distanceField);
        grid.addRow(5, new Label("Base Price (€)"), basePriceField);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setPrefWidth(440);

        dialog.setResultConverter(btn -> {
            if (btn != saveBtn) return null;
            if (routeNameField.getText().trim().isEmpty()) { Toast.error("Route name is required."); return null; }
            if (startCityField.getText().trim().isEmpty()) { Toast.error("Start city is required."); return null; }
            if (endCityField.getText().trim().isEmpty())   { Toast.error("End city is required.");   return null; }
            Route route = new Route();
            route.setRouteName(routeNameField.getText().trim());
            route.setStartCity(startCityField.getText().trim());
            route.setEndCity(endCityField.getText().trim());
            route.setCountry(countryField.getText().trim().isEmpty() ? null : countryField.getText().trim());
            if (!distanceField.getText().trim().isEmpty()) {
                try { route.setDistance(Float.parseFloat(distanceField.getText().trim())); }
                catch (NumberFormatException e) { Toast.error("Distance must be a number."); return null; }
            }
            if (!basePriceField.getText().trim().isEmpty()) {
                try { route.setBasePrice(Double.parseDouble(basePriceField.getText().trim())); }
                catch (NumberFormatException e) { Toast.error("Base price must be a number."); return null; }
            }
            return route;
        });

        return dialog.showAndWait().orElse(null);
    }
}
