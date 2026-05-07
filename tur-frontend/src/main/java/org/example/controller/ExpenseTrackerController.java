package org.example.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.model.Expense;
import org.example.model.Tour;
import org.example.service.ExpenseService;
import org.example.service.TourService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenseTrackerController {

    @FXML private Label totalExpensesValue;
    @FXML private Label totalAmountValue;
    @FXML private Label toursWithExpensesValue;

    @FXML private TableView<Expense> expensesTable;
    @FXML private TableColumn<Expense, String> colExpTour;
    @FXML private TableColumn<Expense, String> colExpCategory;
    @FXML private TableColumn<Expense, Number> colExpAmount;
    @FXML private TableColumn<Expense, String> colExpDate;
    @FXML private TableColumn<Expense, String> colExpReceipt;

    private final TourService tourService = new TourService();
    private final ExpenseService expenseService = new ExpenseService();

    private final Map<Long, String> expenseToTourName = new HashMap<>();

    @FXML
    public void initialize() {
        colExpTour.setCellValueFactory(cd -> {
            String name = expenseToTourName.getOrDefault(cd.getValue().getExpenseId(), "—");
            return new SimpleStringProperty(name);
        });
        colExpCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colExpAmount.setCellValueFactory(cd -> new SimpleObjectProperty<>(cd.getValue().getAmount()));
        colExpDate.setCellValueFactory(cd -> {
            var d = cd.getValue().getDate();
            return new SimpleStringProperty(d != null ? d.toString() : "—");
        });
        colExpReceipt.setCellValueFactory(new PropertyValueFactory<>("receiptPath"));

        loadData();
    }

    @FXML
    public void refresh() {
        loadData();
    }

    private void loadData() {
        new Thread(() -> {
            try {
                List<Tour> tours = tourService.getAllTours();
                List<Expense> allExpenses = new ArrayList<>();
                Map<Long, String> nameMap = new HashMap<>();
                for (Tour t : tours) {
                    List<Expense> tourExpenses = expenseService.getExpensesForTour(t);
                    if (tourExpenses != null) {
                        for (Expense e : tourExpenses) {
                            if (e.getExpenseId() != null) nameMap.put(e.getExpenseId(), t.getTourName());
                            allExpenses.add(e);
                        }
                    }
                }
                double total = expenseService.calculateTotal(allExpenses);
                long toursWithExp = tours.stream()
                        .filter(t -> t.getExpenses() != null && !t.getExpenses().isEmpty()).count();
                javafx.application.Platform.runLater(() -> {
                    expenseToTourName.putAll(nameMap);
                    expensesTable.getItems().setAll(allExpenses);
                    totalExpensesValue.setText(String.valueOf(allExpenses.size()));
                    totalAmountValue.setText("€" + String.format("%,.0f", total));
                    toursWithExpensesValue.setText(String.valueOf(toursWithExp));
                });
            } catch (Exception e) {
                e.printStackTrace();
                javafx.application.Platform.runLater(() -> {
                    totalExpensesValue.setText("—");
                    totalAmountValue.setText("—");
                    toursWithExpensesValue.setText("—");
                });
            }
        }).start();
    }
}