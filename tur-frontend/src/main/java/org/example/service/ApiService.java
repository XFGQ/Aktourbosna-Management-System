package org.example.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.net.URI;
import java.net.http.*;
import java.util.List;
import org.example.model.Tour; // Model sınıfını buraya da kopyala

public class ApiService {
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    private final String BASE_URL = "http://localhost:8080/api/tours";

    public List<Tour> fetchTours() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Gelen JSON metnini Java Listesine çeviriyoruz
        return gson.fromJson(response.body(), new TypeToken<List<Tour>>(){}.getType());
    }
}