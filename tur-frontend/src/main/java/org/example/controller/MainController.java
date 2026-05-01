package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.model.Tour;
import org.example.service.ApiService;
import java.util.List;

public class MainController {
    @FXML private TableView<Tour> tourTable;
    @FXML private TableColumn<Tour, String> nameColumn;
    @FXML private TableColumn<Tour, String> routeColumn;
    @FXML private TableColumn<Tour, Double> priceColumn;

    private ApiService apiService = new ApiService();

    @FXML
    public void initialize() {
        // Tablo sütunlarını Model (Tour) sınıfındaki değişkenlerle eşleştiriyoruz
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("tourName"));
        routeColumn.setCellValueFactory(new PropertyValueFactory<>("route"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        loadTourData();
    }

    private void loadTourData() {
        try {
            List<Tour> tours = apiService.fetchTours();
            tourTable.getItems().setAll(tours);
        } catch (Exception e) {
            e.printStackTrace();
            // Burada kullanıcıya hata mesajı gösteren bir Alert ekleyebiliriz
        }
    }
}