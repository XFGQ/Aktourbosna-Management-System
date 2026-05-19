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
import org.example.model.Expense;
import org.example.model.Tour;
import org.example.service.ExpenseService;
import org.example.service.SessionManager;
import org.example.service.TourService;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

public class ExpenseTrackerController {

    @FXML private Label totalExpensesValue;
    @FXML private Label totalAmountValue;
    @FXML private Label toursWithExpensesValue;

    @FXML private TableView<Expense>       expensesTable;
    @FXML private TableColumn<Expense, String>  colExpTour;
    @FXML private TableColumn<Expense, String>  colExpCategory;
    @FXML private TableColumn<Expense, Number>  colExpAmount;
    @FXML private TableColumn<Expense, String>  colExpDate;
    @FXML private TableColumn<Expense, String>  colExpReceipt;
    @FXML private TableColumn<Expense, Expense> colExpActions;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    private final TourService    tourService    = new TourService();
    private final ExpenseService expenseService = new ExpenseService();

    private final Map<Long, String> expenseToTourName = new HashMap<>();
    private final Map<Long, Long>   expenseToTourId   = new HashMap<>();
    private List<Tour> cachedTours = new ArrayList<>();

    @FXML
    public void initialize() {
        colExpTour.setCellValueFactory(cd -> new SimpleStringProperty(
                expenseToTourName.getOrDefault(cd.getValue().getExpenseId(), "—")));

        colExpCategory.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getCategory() != null ? cd.getValue().getCategory() : "—"));

        colExpAmount.setCellValueFactory(cd -> new SimpleObjectProperty<>(cd.getValue().getAmount()));

        colExpDate.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getDate() != null ? cd.getValue().getDate().format(DATE_FMT) : "—"));

        colExpReceipt.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getReceiptPath() != null ? cd.getValue().getReceiptPath() : "—"));

        colExpActions.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue()));
        colExpActions.setCellFactory(col -> new TableCell<>() {
            private final Button deleteBtn = new Button("Delete");
            private final HBox box = new HBox(deleteBtn);
            {
                box.setAlignment(Pos.CENTER);
                deleteBtn.getStyleClass().add("btn-danger");
                deleteBtn.setOnAction(e -> onDeleteExpense(getTableView().getItems().get(getIndex())));
            }
            @Override protected void updateItem(Expense item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty || item == null ? null : box);
            }
        });

        loadData();
    }

    @FXML
    public void refresh() { loadData(); }

    private void loadData() {
        CompletableFuture.supplyAsync(() -> {
            try {
                List<Tour> allTours = tourService.getAllTours();
                if (SessionManager.getInstance().isGuide() && SessionManager.getInstance().getGuideId() != null) {
                    Long myGuideId = SessionManager.getInstance().getGuideId();
                    return allTours.stream()
                            .filter(t -> t.getGuideId() != null && t.getGuideId().equals(myGuideId))
                            .collect(Collectors.toList());
                }
                return allTours;
            }
            catch (Exception e) { throw new CompletionException(e); }
        }).whenComplete((tours, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
                javafx.application.Platform.runLater(() -> {
                    totalExpensesValue.setText("—");
                    totalAmountValue.setText("—");
                    toursWithExpensesValue.setText("—");
                });
                return;
            }
            List<CompletableFuture<TourExpenses>> expFuts = tours.stream()
                    .filter(t -> t.getTourId() != null)
                    .map(t -> CompletableFuture.supplyAsync(() -> {
                        try {
                            List<Expense> exps = expenseService.getExpensesByTourId(t.getTourId());
                            return new TourExpenses(t, exps != null ? exps : List.of());
                        } catch (Exception e) {
                            return new TourExpenses(t, List.of());
                        }
                    }))
                    .collect(Collectors.toList());

            CompletableFuture.allOf(expFuts.toArray(new CompletableFuture[0]))
                    .whenComplete((ignored, ex2) -> {
                        List<Expense>     allExpenses = new ArrayList<>();
                        Map<Long, String> nameMap     = new HashMap<>();
                        Map<Long, Long>   idMap       = new HashMap<>();
                        int[] toursWithExp = {0};

                        expFuts.stream().map(CompletableFuture::join).forEach(te -> {
                            if (!te.expenses.isEmpty()) toursWithExp[0]++;
                            for (Expense e : te.expenses) {
                                if (e.getExpenseId() != null) {
                                    nameMap.put(e.getExpenseId(), te.tour.getTourName() != null ? te.tour.getTourName() : "—");
                                    idMap.put(e.getExpenseId(), te.tour.getTourId());
                                }
                                allExpenses.add(e);
                            }
                        });

                        double total = expenseService.calculateTotal(allExpenses);
                        javafx.application.Platform.runLater(() -> {
                            cachedTours = tours;
                            expenseToTourName.clear(); expenseToTourName.putAll(nameMap);
                            expenseToTourId.clear();   expenseToTourId.putAll(idMap);
                            expensesTable.getItems().setAll(allExpenses);
                            totalExpensesValue.setText(String.valueOf(allExpenses.size()));
                            totalAmountValue.setText("€" + String.format("%,.0f", total));
                            toursWithExpensesValue.setText(String.valueOf(toursWithExp[0]));
                        });
                    });
        });
    }

    private static class TourExpenses {
        final Tour          tour;
        final List<Expense> expenses;
        TourExpenses(Tour tour, List<Expense> expenses) { this.tour = tour; this.expenses = expenses; }
    }

    @FXML
    private void onAddExpense() {
        Object[] result = AddExpenseDialog.show(cachedTours);
        if (result == null) return;
        Long tourId = (Long) result[0];
        Expense expense = (Expense) result[1];
        runInBackground(
                () -> expenseService.addExpense(tourId, expense),
                "Adding expense...", "Expense added successfully.", "Failed to add expense");
    }

    private void onDeleteExpense(Expense expense) {
        if (!ConfirmDialog.show("Confirm deletion",
                "Delete this expense (" + expense.getCategory() + " — €" + expense.getAmount() + ")?")) return;
        Long tourId = expenseToTourId.get(expense.getExpenseId());
        if (tourId == null) { Toast.error("Cannot delete: tour not found for this expense."); return; }
        runInBackground(
                () -> expenseService.deleteExpense(tourId, expense.getExpenseId()),
                "Deleting expense...", "Expense deleted.", "Failed to delete expense");
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