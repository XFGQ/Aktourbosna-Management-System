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
        colExpAmount.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Number val, boolean empty) {
                super.updateItem(val, empty);
                if (empty) setText(null);
                else if (val == null) setText("—");
                else setText("€" + String.format("%,.0f", val.doubleValue()));
            }
        });

        colExpDate.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getDate() != null ? cd.getValue().getDate().format(DATE_FMT) : "—"));

        colExpReceipt.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getReceiptPath() != null ? cd.getValue().getReceiptPath() : "—"));

        colExpActions.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue()));
        colExpActions.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn   = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final Button receiptsBtn = new Button("Receipts");
            private final HBox box = new HBox(4, receiptsBtn, editBtn, deleteBtn);
            {
                box.setAlignment(Pos.CENTER);
                receiptsBtn.getStyleClass().add("btn-secondary");
                editBtn.getStyleClass().add("btn-secondary");
                deleteBtn.getStyleClass().add("btn-danger");
                receiptsBtn.setOnAction(e -> onManageReceipts(getTableView().getItems().get(getIndex())));
                editBtn.setOnAction(e   -> onEditExpense(getTableView().getItems().get(getIndex())));
                deleteBtn.setOnAction(e -> onDeleteExpense(getTableView().getItems().get(getIndex())));
            }
            @Override protected void updateItem(Expense item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty || item == null ? null : box);
            }
        });

        setupContextMenu(expensesTable);

        loadData();
    }

    @FXML
    public void refresh() { loadData(); }

    private void setupContextMenu(TableView<Expense> table) {
        table.setRowFactory(tv -> {
            TableRow<Expense> row = new TableRow<>();
            ContextMenu menu = new ContextMenu();
            MenuItem mnuReceipts = new MenuItem("Manage Receipts");
            mnuReceipts.setOnAction(e -> onManageReceipts(row.getItem()));
            MenuItem mnuEdit = new MenuItem("Edit");
            mnuEdit.setOnAction(e -> onEditExpense(row.getItem()));
            MenuItem mnuDelete = new MenuItem("Delete");
            mnuDelete.setOnAction(e -> onDeleteExpense(row.getItem()));
            menu.getItems().addAll(mnuReceipts, mnuEdit, mnuDelete);
            row.contextMenuProperty().bind(
                    javafx.beans.binding.Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(menu)
            );
            return row;
        });
    }

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

    private void onManageReceipts(Expense expense) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Manage Receipt");
        alert.setHeaderText("Receipt options for " + expense.getCategory());
        alert.setContentText("Choose an action:");

        ButtonType btnUpload = new ButtonType("Upload");
        ButtonType btnView = new ButtonType("View");
        ButtonType btnDownload = new ButtonType("Download");
        ButtonType btnDelete = new ButtonType("Delete");
        ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(btnUpload, btnView, btnDownload, btnDelete, btnCancel);

        alert.showAndWait().ifPresent(type -> {
            Long tourId = expenseToTourId.get(expense.getExpenseId());
            if (tourId == null) return;
            
            if (type == btnUpload) {
                javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
                fileChooser.getExtensionFilters().addAll(
                    new javafx.stage.FileChooser.ExtensionFilter("Image/PDF Files", "*.jpg", "*.jpeg", "*.png", "*.pdf")
                );
                java.io.File file = fileChooser.showOpenDialog(null);
                if (file != null) {
                    runInBackground(() -> expenseService.uploadReceipt(tourId, expense.getExpenseId(), file),
                            "Uploading...", "Receipt uploaded.", "Failed to upload");
                }
            } else if (type == btnView) {
                if (expense.getReceiptPath() == null || expense.getReceiptPath().isEmpty() || "-".equals(expense.getReceiptPath())) {
                    Toast.error("No receipt available to view.");
                    return;
                }
                try {
                    String ext = ".tmp";
                    if (expense.getReceiptPath().contains(".")) {
                        ext = expense.getReceiptPath().substring(expense.getReceiptPath().lastIndexOf("."));
                    }
                    java.io.File tempFile = java.io.File.createTempFile("receipt_view_", ext);
                    tempFile.deleteOnExit();
                    runInBackground(() -> {
                        expenseService.downloadReceipt(tourId, expense.getExpenseId(), tempFile);
                        try {
                            java.awt.Desktop.getDesktop().open(tempFile);
                        } catch (Exception ex) {
                            throw new RuntimeException("Dosya açılamadı. İşletim sistemi bu eylemi desteklemiyor olabilir.", ex);
                        }
                    }, "Loading receipt...", "Receipt opened.", "Failed to open receipt");
                } catch (Exception e) {
                    Toast.error("Error creating temp file: " + e.getMessage());
                }
            } else if (type == btnDownload) {
                if (expense.getReceiptPath() == null || expense.getReceiptPath().isEmpty() || "-".equals(expense.getReceiptPath())) {
                    Toast.error("No receipt available for this expense.");
                    return;
                }
                javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
                String ext = "";
                if (expense.getReceiptPath().contains(".")) {
                    ext = expense.getReceiptPath()
                            .substring(expense.getReceiptPath().lastIndexOf("."))
                            .toLowerCase();
                }
                String fileName = "receipt_" + expense.getExpenseId() + ext;
                fileChooser.setInitialFileName(fileName);
                if (!ext.isEmpty()) {
                    String extUpper = ext.substring(1).toUpperCase();
                    fileChooser.getExtensionFilters().add(
                        new javafx.stage.FileChooser.ExtensionFilter(extUpper + " Files (*" + ext + ")", "*" + ext)
                    );
                }
                fileChooser.getExtensionFilters().add(
                    new javafx.stage.FileChooser.ExtensionFilter("All Files", "*.*")
                );
                java.io.File saveFile = fileChooser.showSaveDialog(null);
                if (saveFile != null) {
                    final String finalExt = ext;
                    java.io.File target = (!finalExt.isEmpty() && !saveFile.getName().toLowerCase().endsWith(finalExt))
                            ? new java.io.File(saveFile.getAbsolutePath() + finalExt)
                            : saveFile;
                    runInBackground(() -> expenseService.downloadReceipt(tourId, expense.getExpenseId(), target),
                            "Downloading...", "Receipt downloaded.", "Failed to download");
                }
            } else if (type == btnDelete) {
                if (expense.getReceiptPath() == null || expense.getReceiptPath().isEmpty() || "-".equals(expense.getReceiptPath())) {
                    Toast.error("No receipt available to delete.");
                    return;
                }
                if (ConfirmDialog.show("Delete Receipt", "Are you sure?")) {
                    runInBackground(() -> expenseService.deleteReceipt(tourId, expense.getExpenseId()),
                            "Deleting...", "Receipt deleted.", "Failed to delete");
                }
            }
        });
    }

    @FXML
    private void onAddExpense() {
        Object[] result = AddExpenseDialog.show(cachedTours);
        if (result == null) return;
        Long tourId = (Long) result[0];
        Expense expense = (Expense) result[1];
        java.io.File fileToUpload = result.length > 2 ? (java.io.File) result[2] : null;

        runInBackground(() -> {
            Expense created = expenseService.addExpense(tourId, expense);
            if (fileToUpload != null && created != null && created.getExpenseId() != null) {
                expenseService.uploadReceipt(tourId, created.getExpenseId(), fileToUpload);
            }
        }, "Adding expense...", "Expense added successfully.", "Failed to add expense");
    }

    private void onEditExpense(Expense expense) {
        Long tourId = expenseToTourId.get(expense.getExpenseId());
        if (tourId == null) { Toast.error("Cannot edit: tour not found for this expense."); return; }
        Object[] result = AddExpenseDialog.show(expense, tourId, cachedTours);
        if (result == null) return;
        java.io.File fileToUpload = result.length > 2 ? (java.io.File) result[2] : null;

        runInBackground(() -> {
            Expense updated = expenseService.updateExpense(tourId, expense.getExpenseId(), (Expense) result[1]);
            if (fileToUpload != null && updated != null) {
                expenseService.uploadReceipt(tourId, updated.getExpenseId(), fileToUpload);
            }
        }, "Updating expense...", "Expense updated successfully.", "Failed to update expense");
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
        task.setOnSucceeded(e -> {
            loadingStage.close();
            loadData();
            AppController app = AppController.getInstance();
            if (app != null) app.invalidateOtherViews("expenseTracker.fxml");
            Toast.success(successMsg);
        });
        task.setOnFailed(e -> {
            loadingStage.close();
            Toast.error(errorTitle + ": " + task.getException().getMessage());
        });
        new Thread(task).start();
    }

    @FunctionalInterface
    private interface BackgroundOp { void run() throws Exception; }
}