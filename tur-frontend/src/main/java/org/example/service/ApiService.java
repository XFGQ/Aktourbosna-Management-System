package org.example.service;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.example.model.Guide;
import org.example.model.Tour;
import org.example.model.Vehicle;

import java.net.URI;
import java.net.http.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ApiService {

    private static final String BASE_URL = "http://localhost:8080/api";

    private final HttpClient client = HttpClient.newHttpClient();

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>)
                    (json, type, ctx) -> LocalDate.parse(json.getAsString()))
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>)
                    (json, type, ctx) -> LocalDateTime.parse(json.getAsString()))
            .create();

    public List<Tour> fetchTours() throws Exception {
        return gson.fromJson(get(BASE_URL + "/tours"),
                new TypeToken<List<Tour>>() {}.getType());
    }

    public List<Vehicle> fetchVehicles() throws Exception {
        return gson.fromJson(get(BASE_URL + "/vehicles"),
                new TypeToken<List<Vehicle>>() {}.getType());
    }

    public List<Guide> fetchGuides() throws Exception {
        return gson.fromJson(get(BASE_URL + "/guides"),
                new TypeToken<List<Guide>>() {}.getType());
    }

    private String get(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}