package org.example.service;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.JsonParser;
import org.example.model.Customer;
import org.example.model.Expense;
import org.example.model.Guide;
import org.example.model.Route;
import org.example.model.Tour;
import org.example.model.Vehicle;

import java.net.URI;
import java.net.http.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ApiService {

    private static final String BASE_URL = "https://aktour.rinnesoft.com/api";
    private static String authToken = null;

    public static void setToken(String token) { authToken = token; }
    public static String getToken() { return authToken; }

    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(java.time.Duration.ofSeconds(5))
            .build();

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>)
                    (json, type, ctx) -> LocalDate.parse(json.getAsString()))
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>)
                    (json, type, ctx) -> LocalDateTime.parse(json.getAsString()))
            .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>)
                    (src, type, ctx) -> new JsonPrimitive(src.toString()))
            .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>)
                    (src, type, ctx) -> new JsonPrimitive(src.toString()))
            .create();

    public List<Tour> fetchTours() throws Exception {
        List<Tour> summaries = gson.fromJson(get(BASE_URL + "/tours"),
                new TypeToken<List<Tour>>() {}.getType());
        if (summaries == null) return new ArrayList<>();

        long start = System.currentTimeMillis();
        List<CompletableFuture<Tour>> futures = summaries.stream()
                .filter(t -> t.getTourId() != null)
                .map(t -> {
                    HttpRequest req = authorizedBuilder(BASE_URL + "/tours/" + t.getTourId()).GET().build();
                    return client.sendAsync(req, HttpResponse.BodyHandlers.ofString())
                            .thenApply(r -> {
                                Tour detail = gson.fromJson(r.body(), Tour.class);
                                return detail != null ? detail : t;
                            })
                            .exceptionally(e -> t);
                })
                .collect(Collectors.toList());

        List<Tour> result = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
        System.out.println("[GET /tours/{id} x" + result.size() + "] took " + (System.currentTimeMillis() - start) + " ms (parallel)");
        return result;
    }

    public List<Vehicle> fetchVehicles() throws Exception {
        List<Vehicle> summaries = gson.fromJson(get(BASE_URL + "/vehicles"),
                new TypeToken<List<Vehicle>>() {}.getType());
        if (summaries == null) return new ArrayList<>();

        List<CompletableFuture<Vehicle>> futures = summaries.stream()
                .filter(v -> v.getId() != null)
                .map(v -> {
                    HttpRequest req = authorizedBuilder(BASE_URL + "/vehicles/" + v.getId()).GET().build();
                    return client.sendAsync(req, HttpResponse.BodyHandlers.ofString())
                            .thenApply(r -> {
                                Vehicle detail = gson.fromJson(r.body(), Vehicle.class);
                                return detail != null ? detail : v;
                            })
                            .exceptionally(e -> v);
                })
                .collect(Collectors.toList());

        return futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
    }

    public List<Guide> fetchGuides() throws Exception {
        List<Guide> summaries = gson.fromJson(get(BASE_URL + "/guides"),
                new TypeToken<List<Guide>>() {}.getType());
        if (summaries == null) return new ArrayList<>();
        // GET /api/guides returns GuideSummaryDTO (no email/phone) — fetch full details in parallel
        List<CompletableFuture<Guide>> futures = summaries.stream()
                .filter(g -> g.getId() != null)
                .map(g -> {
                    HttpRequest req = authorizedBuilder(BASE_URL + "/guides/" + g.getId()).GET().build();
                    return client.sendAsync(req, HttpResponse.BodyHandlers.ofString())
                            .thenApply(r -> {
                                Guide detail = gson.fromJson(r.body(), Guide.class);
                                return detail != null ? detail : g;
                            })
                            .exceptionally(e -> g);
                })
                .collect(Collectors.toList());
        return futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
    }

    public List<Expense> fetchExpensesByTour(Long tourId) throws Exception {
        return gson.fromJson(get(BASE_URL + "/tours/" + tourId + "/expenses"), new TypeToken<List<Expense>>() {}.getType());
    }

    public Vehicle createVehicle(Vehicle vehicle) throws Exception {
        String json = gson.toJson(vehicle);
        String response = post(BASE_URL + "/vehicles", json);
        return gson.fromJson(response, Vehicle.class);
    }

    public Vehicle updateVehicle(Long id, Vehicle vehicle) throws Exception {
        String json = gson.toJson(vehicle);
        String response = put(BASE_URL + "/vehicles/" + id, json);
        return gson.fromJson(response, Vehicle.class);
    }

    public void deleteVehicle(Long id) throws Exception {
        delete(BASE_URL + "/vehicles/" + id);
    }

    public Guide getGuideMe() throws Exception {
        return gson.fromJson(get(BASE_URL + "/guides/me"), Guide.class);
    }

    public Guide updateGuideMe(Guide guide) throws Exception {
        String json = gson.toJson(guide);
        String response = put(BASE_URL + "/guides/me", json);
        return gson.fromJson(response, Guide.class);
    }

    public Guide createGuide(Guide guide) throws Exception {
        String json = gson.toJson(guide);
        String response = post(BASE_URL + "/guides", json);
        return gson.fromJson(response, Guide.class);
    }

    public Guide updateGuide(Long id, Guide guide) throws Exception {
        String json = gson.toJson(guide);
        String response = put(BASE_URL + "/guides/" + id, json);
        return gson.fromJson(response, Guide.class);
    }

    public void deleteGuide(Long id) throws Exception {
        delete(BASE_URL + "/guides/" + id);
    }

    public Tour createTour(Tour tour) throws Exception {
        String json = gson.toJson(tour);
        String response = post(BASE_URL + "/tours", json);
        return gson.fromJson(response, Tour.class);
    }

    public Tour updateTour(Long id, Tour tour) throws Exception {
        String json = gson.toJson(tour);
        String response = put(BASE_URL + "/tours/" + id, json);
        return gson.fromJson(response, Tour.class);
    }

    public void deleteTour(Long id) throws Exception {
        delete(BASE_URL + "/tours/" + id);
    }

    public Expense createExpense(Long tourId, Expense expense) throws Exception {
        String json = gson.toJson(expense);
        String response = post(BASE_URL + "/tours/" + tourId + "/expenses", json);
        return gson.fromJson(response, Expense.class);
    }

    public Expense updateExpense(Long tourId, Long expenseId, Expense expense) throws Exception {
        String json = gson.toJson(expense);
        String response = put(BASE_URL + "/tours/" + tourId + "/expenses/" + expenseId, json);
        return gson.fromJson(response, Expense.class);
    }

    public void deleteExpense(Long tourId, Long expenseId) throws Exception {
        delete(BASE_URL + "/tours/" + tourId + "/expenses/" + expenseId);
    }

    public Expense uploadReceipt(Long tourId, Long expenseId, File file) throws Exception {
        String boundary = "Boundary-" + System.currentTimeMillis();
        byte[] fileBytes = Files.readAllBytes(file.toPath());
        String head = "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"\r\n" +
                "Content-Type: application/octet-stream\r\n\r\n";
        String tail = "\r\n--" + boundary + "--\r\n";
        
        byte[] headBytes = head.getBytes();
        byte[] tailBytes = tail.getBytes();
        byte[] body = new byte[headBytes.length + fileBytes.length + tailBytes.length];
        System.arraycopy(headBytes, 0, body, 0, headBytes.length);
        System.arraycopy(fileBytes, 0, body, headBytes.length, fileBytes.length);
        System.arraycopy(tailBytes, 0, body, headBytes.length + fileBytes.length, tailBytes.length);

        HttpRequest request = authorizedBuilder(BASE_URL + "/tours/" + tourId + "/expenses/" + expenseId + "/receipt")
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(body))
                .build();
        
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) throw new RuntimeException("HTTP " + response.statusCode() + ": " + response.body());
        return gson.fromJson(response.body(), Expense.class);
    }

    public Expense deleteReceipt(Long tourId, Long expenseId) throws Exception {
        HttpRequest request = authorizedBuilder(BASE_URL + "/tours/" + tourId + "/expenses/" + expenseId + "/receipt")
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) throw new RuntimeException("HTTP " + response.statusCode() + ": " + response.body());
        return gson.fromJson(response.body(), Expense.class);
    }

    public void downloadReceipt(Long tourId, Long expenseId, File saveFile) throws Exception {
        HttpRequest request = authorizedBuilder(BASE_URL + "/tours/" + tourId + "/expenses/" + expenseId + "/receipt")
                .GET()
                .build();
        HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
        if (response.statusCode() >= 400) throw new RuntimeException("Failed to download receipt");
        try (InputStream in = response.body()) {
            Files.copy(in, saveFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public List<Customer> fetchCustomers(Long tourId) throws Exception {
        return gson.fromJson(get(BASE_URL + "/tours/" + tourId + "/customers"),
                new TypeToken<List<Customer>>() {}.getType());
    }

    public Customer createCustomer(Long tourId, Customer customer) throws Exception {
        String json = gson.toJson(customer);
        String response = post(BASE_URL + "/tours/" + tourId + "/customers", json);
        return gson.fromJson(response, Customer.class);
    }

    public Customer updateCustomer(Long tourId, Long customerId, Customer customer) throws Exception {
        String json = gson.toJson(customer);
        String response = put(BASE_URL + "/tours/" + tourId + "/customers/" + customerId, json);
        return gson.fromJson(response, Customer.class);
    }

    public void deleteCustomer(Long tourId, Long customerId) throws Exception {
        delete(BASE_URL + "/tours/" + tourId + "/customers/" + customerId);
    }

    public List<Route> fetchRoutes() throws Exception {
        List<Route> summaries = gson.fromJson(get(BASE_URL + "/routes"),
                new TypeToken<List<Route>>() {}.getType());
        if (summaries == null) return new ArrayList<>();
        // GET /api/routes returns RouteSummaryDTO (no basePrice) — fetch full details in parallel
        List<CompletableFuture<Route>> futures = summaries.stream()
                .filter(r -> r.getRouteId() != null)
                .map(r -> {
                    HttpRequest req = authorizedBuilder(BASE_URL + "/routes/" + r.getRouteId()).GET().build();
                    return client.sendAsync(req, HttpResponse.BodyHandlers.ofString())
                            .thenApply(resp -> {
                                Route detail = gson.fromJson(resp.body(), Route.class);
                                return detail != null ? detail : r;
                            })
                            .exceptionally(e -> r);
                })
                .collect(Collectors.toList());
        return futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
    }

    public Route createRoute(Route route) throws Exception {
        String json = gson.toJson(route);
        String response = post(BASE_URL + "/routes", json);
        return gson.fromJson(response, Route.class);
    }

    public Route updateRoute(Long id, Route route) throws Exception {
        String json = gson.toJson(route);
        String response = put(BASE_URL + "/routes/" + id, json);
        return gson.fromJson(response, Route.class);
    }

    public void deleteRoute(Long id) throws Exception {
        delete(BASE_URL + "/routes/" + id);
    }

    public String[] login(String username, String password) throws Exception {
        String body = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/auth/login"))
                .timeout(java.time.Duration.ofSeconds(6))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (java.net.ConnectException | java.net.http.HttpTimeoutException e) {
            throw new RuntimeException("Cannot connect to server. Make sure the backend is running.");
        }
        if (response.statusCode() >= 400) {
            throw new RuntimeException("Invalid username or password.");
        }
        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
        String token = json.get("token").getAsString();
        String role  = json.get("role").getAsString();
        ApiService.setToken(token);
        return new String[]{token, role};
    }

    private HttpRequest.Builder authorizedBuilder(String url) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(java.time.Duration.ofSeconds(15));
        if (authToken != null) builder.header("Authorization", "Bearer " + authToken);
        return builder;
    }

    private String get(String url) throws Exception {
        long start = System.currentTimeMillis();
        HttpRequest request = authorizedBuilder(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("[GET " + url + "] took " + (System.currentTimeMillis() - start) + " ms, status=" + response.statusCode());
        if (response.statusCode() >= 400) {
            throw new RuntimeException("HTTP " + response.statusCode() + ": " + response.body());
        }
        return response.body();
    }

    private String post(String url, String jsonBody) throws Exception {
        HttpRequest request = authorizedBuilder(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) throw new RuntimeException("HTTP " + response.statusCode() + ": " + response.body());
        return response.body();
    }

    private String put(String url, String jsonBody) throws Exception {
        HttpRequest request = authorizedBuilder(url)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) throw new RuntimeException("HTTP " + response.statusCode() + ": " + response.body());
        return response.body();
    }

    private void delete(String url) throws Exception {
        HttpRequest request = authorizedBuilder(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) throw new RuntimeException("HTTP " + response.statusCode() + ": " + response.body());
    }
}