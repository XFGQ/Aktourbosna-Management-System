package org.example.controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.model.Customer;
import org.example.model.Tour;
import org.example.service.CustomerService;

import java.util.List;

public class TourCustomersDialog {

    public static void show(Tour tour) {
        Stage stage = new Stage();
        stage.setTitle("Customers — " + tour.getTourName());
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UTILITY);
        stage.setResizable(false);

        CustomerService customerService = new CustomerService();

        Label countLabel = new Label("Loading...");
        countLabel.setStyle("-fx-text-fill: #555; -fx-font-size: 13px;");

        TableView<Customer> table = new TableView<>();
        table.setPrefWidth(580);
        table.setPrefHeight(300);
        table.setFixedCellSize(40);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Customer, String> colName = new TableColumn<>("Full Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colName.setMinWidth(160);

        TableColumn<Customer, String> colPassport = new TableColumn<>("Passport No");
        colPassport.setCellValueFactory(new PropertyValueFactory<>("passportNumber"));
        colPassport.setMinWidth(110);

        TableColumn<Customer, String> colPhone = new TableColumn<>("Phone");
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colPhone.setMinWidth(120);

        TableColumn<Customer, String> colNationality = new TableColumn<>("Nationality");
        colNationality.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getNationality() != null ? cd.getValue().getNationality() : "—"));
        colNationality.setMinWidth(100);

        TableColumn<Customer, Customer> colActions = new TableColumn<>("Actions");
        colActions.setMinWidth(80);
        colActions.setSortable(false);
        colActions.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue()));
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button delBtn = new Button("Delete");
            {
                delBtn.getStyleClass().add("btn-danger");
                delBtn.setOnAction(e -> {
                    Customer c = getTableView().getItems().get(getIndex());
                    if (!ConfirmDialog.show("Confirm deletion",
                            "Delete customer \"" + c.getFullName() + "\"?")) return;
                    runOp(() -> customerService.deleteCustomer(tour.getTourId(), c.getId()),
                            "Customer deleted.", "Failed to delete",
                            table, countLabel, customerService, tour.getTourId());
                });
            }
            @Override protected void updateItem(Customer c, boolean empty) {
                super.updateItem(c, empty);
                setGraphic(empty || c == null ? null : delBtn);
            }
        });

        table.getColumns().addAll(colName, colPassport, colPhone, colNationality, colActions);

        Button addBtn = new Button("+ Add Customer");
        addBtn.getStyleClass().add("btn-primary");
        addBtn.setOnAction(e -> {
            Customer newCustomer = AddCustomerDialog.show();
            if (newCustomer == null) return;
            runOp(() -> customerService.addCustomer(tour.getTourId(), newCustomer),
                    "Customer added successfully.", "Failed to add customer",
                    table, countLabel, customerService, tour.getTourId());
        });

        Pane spacer = new Pane();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        HBox header = new HBox(10, countLabel, spacer, addBtn);
        header.setAlignment(Pos.CENTER_LEFT);

        Button closeBtn = new Button("Close");
        closeBtn.setOnAction(e -> stage.close());
        HBox closeRow = new HBox(closeBtn);
        closeRow.setAlignment(Pos.CENTER_RIGHT);

        VBox root = new VBox(14, header, table, closeRow);
        root.setPadding(new Insets(20, 24, 20, 24));
        root.setStyle("-fx-background-color: #f0f2f5;");

        Scene scene = new Scene(root);
        try {
            String css = TourCustomersDialog.class.getResource("/styles/styles.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception ignored) {}

        stage.setScene(scene);
        stage.show();

        loadCustomers(table, countLabel, customerService, tour.getTourId());
    }

    private static void loadCustomers(TableView<Customer> table, Label countLabel,
                                       CustomerService customerService, Long tourId) {
        Task<List<Customer>> task = new Task<>() {
            @Override protected List<Customer> call() throws Exception {
                return customerService.getCustomers(tourId);
            }
        };
        task.setOnSucceeded(e -> {
            List<Customer> list = task.getValue();
            table.getItems().setAll(list);
            countLabel.setText(list.size() + " customer" + (list.size() == 1 ? "" : "s"));
        });
        task.setOnFailed(e -> countLabel.setText("Failed to load"));
        new Thread(task).start();
    }

    @FunctionalInterface
    private interface Op { void run() throws Exception; }

    private static void runOp(Op op, String successMsg, String errorTitle,
                               TableView<Customer> table, Label countLabel,
                               CustomerService customerService, Long tourId) {
        Task<Void> task = new Task<>() {
            @Override protected Void call() throws Exception { op.run(); return null; }
        };
        task.setOnSucceeded(e -> {
            Toast.success(successMsg);
            loadCustomers(table, countLabel, customerService, tourId);
        });
        task.setOnFailed(e -> Toast.error(errorTitle + ": " + task.getException().getMessage()));
        new Thread(task).start();
    }
}
