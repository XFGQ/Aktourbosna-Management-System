package org.example.service;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.JsonParser;
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

    private static String authToken = null;

    public static void setToken(String token) { authToken = token; }
    public static String getToken() { return authToken; }

    private final HttpClient client = HttpClient.newHttpClient();

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

    public String[] login(String username, String password) throws Exception {
        String body = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) {
            throw new RuntimeException("Kullanıcı adı veya şifre hatalı.");
        }
        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
        String token = json.get("token").getAsString();
        String role  = json.get("role").getAsString();
        return new String[]{token, role};
    }

    private HttpRequest.Builder authorizedBuilder(String url) {
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(url));
        if (authToken != null) builder.header("Authorization", "Bearer " + authToken);
        return builder;
    }

    private String get(String url) throws Exception {
        long start = System.currentTimeMillis();
        HttpRequest request = authorizedBuilder(url).GET().build();
        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());
        long elapsed = System.currentTimeMillis() - start;
        System.out.println("[GET " + url + "] took " + elapsed + " ms");
        return response.body();
    }

    private String post(String url, String jsonBody) throws Exception {
        long start = System.currentTimeMillis();
        HttpRequest request = authorizedBuilder(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());
        long elapsed = System.currentTimeMillis() - start;
        System.out.println("[POST " + url + "] took " + elapsed + " ms, status=" + response.statusCode());
        if (response.statusCode() >= 400) {
            throw new RuntimeException("HTTP " + response.statusCode() + ": " + response.body());
        }
        return response.body();
    }

    private String put(String url, String jsonBody) throws Exception {
        long start = System.currentTimeMillis();
        HttpRequest request = authorizedBuilder(url)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());
        long elapsed = System.currentTimeMillis() - start;
        System.out.println("[PUT " + url + "] package org.example.service;\r\n" + //
                                "\r\n" + //
                                "import com.google.gson.*;\r\n" + //
                                "import com.google.gson.reflect.TypeToken;\r\n" + //
                                "import org.example.model.Guide;\r\n" + //
                                "import org.example.model.Tour;\r\n" + //
                                "import org.example.model.Vehicle;\r\n" + //
                                "\r\n" + //
                                "import java.net.URI;\r\n" + //
                                "import java.net.http.*;\r\n" + //
                                "import java.time.LocalDate;\r\n" + //
                                "import java.time.LocalDateTime;\r\n" + //
                                "import java.util.List;\r\n" + //
                                "\r\n" + //
                                "public class ApiService {\r\n" + //
                                "\r\n" + //
                                "    private static final String BASE_URL = \"http://localhost:8080/api\";\r\n" + //
                                "\r\n" + //
                                "    private final HttpClient client = HttpClient.newHttpClient();\r\n" + //
                                "\r\n" + //
                                "    private final Gson gson = new GsonBuilder()\r\n" + //
                                "            .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>)\r\n" + //
                                "                    (json, type, ctx) -> LocalDate.parse(json.getAsString()))\r\n" + //
                                "            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>)\r\n" + //
                                "                    (json, type, ctx) -> LocalDateTime.parse(json.getAsString()))\r\n" + //
                                "            .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>)\r\n" + //
                                "                    (src, type, ctx) -> new JsonPrimitive(src.toString()))\r\n" + //
                                "            .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>)\r\n" + //
                                "                    (src, type, ctx) -> new JsonPrimitive(src.toString()))\r\n" + //
                                "            .create();\r\n" + //
                                "\r\n" + //
                                "    public List<Tour> fetchTours() throws Exception {\r\n" + //
                                "        return gson.fromJson(get(BASE_URL + \"/tours\"),\r\n" + //
                                "                new TypeToken<List<Tour>>() {}.getType());\r\n" + //
                                "    }\r\n" + //
                                "\r\n" + //
                                "    public List<Vehicle> fetchVehicles() throws Exception {\r\n" + //
                                "        return gson.fromJson(get(BASE_URL + \"/vehicles\"),\r\n" + //
                                "                new TypeToken<List<Vehicle>>() {}.getType());\r\n" + //
                                "    }\r\n" + //
                                "\r\n" + //
                                "    public List<Guide> fetchGuides() throws Exception {\r\n" + //
                                "        return gson.fromJson(get(BASE_URL + \"/guides\"),\r\n" + //
                                "                new TypeToken<List<Guide>>() {}.getType());\r\n" + //
                                "    }\r\n" + //
                                "\r\n" + //
                                "    public Vehicle createVehicle(Vehicle vehicle) throws Exception {\r\n" + //
                                "        String json = gson.toJson(vehicle);\r\n" + //
                                "        String response = post(BASE_URL + \"/vehicles\", json);\r\n" + //
                                "        return gson.fromJson(response, Vehicle.class);\r\n" + //
                                "    }\r\n" + //
                                "\r\n" + //
                                "    public Vehicle updateVehicle(Long id, Vehicle vehicle) throws Exception {\r\n" + //
                                "        String json = gson.toJson(vehicle);\r\n" + //
                                "        String response = put(BASE_URL + \"/vehicles/\" + id, json);\r\n" + //
                                "        return gson.fromJson(response, Vehicle.class);\r\n" + //
                                "    }\r\n" + //
                                "\r\n" + //
                                "    public void deleteVehicle(Long id) throws Exception {\r\n" + //
                                "        delete(BASE_URL + \"/vehicles/\" + id);\r\n" + //
                                "    }\r\n" + //
                                "\r\n" + //
                                "    public Guide createGuide(Guide guide) throws Exception {\r\n" + //
                                "        String json = gson.toJson(guide);\r\n" + //
                                "        String response = post(BASE_URL + \"/guides\", json);\r\n" + //
                                "        return gson.fromJson(response, Guide.class);\r\n" + //
                                "    }\r\n" + //
                                "\r\n" + //
                                "    private String get(String url) throws Exception {\r\n" + //
                                "        long start = System.currentTimeMillis();\r\n" + //
                                "        HttpRequest request = HttpRequest.newBuilder()\r\n" + //
                                "                .uri(URI.create(url))\r\n" + //
                                "                .GET()\r\n" + //
                                "                .build();\r\n" + //
                                "        HttpResponse<String> response = client.send(request,\r\n" + //
                                "                HttpResponse.BodyHandlers.ofString());\r\n" + //
                                "        long elapsed = System.currentTimeMillis() - start;\r\n" + //
                                "        System.out.println(\"[GET \" + url + \"] took \" + elapsed + \" ms\");\r\n" + //
                                "        return response.body();\r\n" + //
                                "    }\r\n" + //
                                "\r\n" + //
                                "    private String post(String url, String jsonBody) throws Exception {\r\n" + //
                                "        long start = System.currentTimeMillis();\r\n" + //
                                "        HttpRequest request = HttpRequest.newBuilder()\r\n" + //
                                "                .uri(URI.create(url))\r\n" + //
                                "                .header(\"Content-Type\", \"application/json\")\r\n" + //
                                "                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))\r\n" + //
                                "                .build();\r\n" + //
                                "        HttpResponse<String> response = client.send(request,\r\n" + //
                                "                HttpResponse.BodyHandlers.ofString());\r\n" + //
                                "        long elapsed = System.currentTimeMillis() - start;\r\n" + //
                                "        System.out.println(\"[POST \" + url + \"] took \" + elapsed + \" ms, status=\" + response.statusCode());\r\n" + //
                                "        if (response.statusCode() >= 400) {\r\n" + //
                                "            throw new RuntimeException(\"HTTP \" + response.statusCode() + \": \" + response.body());\r\n" + //
                                "        }\r\n" + //
                                "        return response.body();\r\n" + //
                                "    }\r\n" + //
                                "\r\n" + //
                                "    private String put(String url, String jsonBody) throws Exception {\r\n" + //
                                "        long start = System.currentTimeMillis();\r\n" + //
                                "        HttpRequest request = HttpRequest.newBuilder()\r\n" + //
                                "                .uri(URI.create(url))\r\n" + //
                                "                .header(\"Content-Type\", \"application/json\")\r\n" + //
                                "                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))\r\n" + //
                                "                .build();\r\n" + //
                                "        HttpResponse<String> response = client.send(request,\r\n" + //
                                "                HttpResponse.BodyHandlers.ofString());\r\n" + //
                                "        long elapsed = System.currentTimeMillis() - start;\r\n" + //
                                "        System.out.println(\"[PUT \" + url + \"] took \" + elapsed + \" ms, status=\" + response.statusCode());\r\n" + //
                                "        if (response.statusCode() >= 400) {\r\n" + //
                                "            throw new RuntimeException(\"HTTP \" + response.statusCode() + \": \" + response.body());\r\n" + //
                                "        }\r\n" + //
                                "        return response.body();\r\n" + //
                                "    }\r\n" + //
                                "\r\n" + //
                                "    private void delete(String url) throws Exception {\r\n" + //
                                "        long start = System.currentTimeMillis();\r\n" + //
                                "        HttpRequest request = HttpRequest.newBuilder()\r\n" + //
                                "                .uri(URI.create(url))\r\n" + //
                                "                .DELETE()\r\n" + //
                                "                .build();\r\n" + //
                                "        HttpResponse<String> response = client.send(request,\r\n" + //
                                "                HttpResponse.BodyHandlers.ofString());\r\n" + //
                                "        long elapsed = System.currentTimeMillis() - start;\r\n" + //
                                "        System.out.println(\"[DELETE \" + url + \"] took \" + elapsed + \" ms, status=\" + response.statusCode());\r\n" + //
                                "        if (response.statusCode() >= 400) {\r\n" + //
                                "            throw new RuntimeException(\"HTTP \" + response.statusCode() + \": \" + response.body());\r\n" + //
                                "        }\r\n" + //
                                "    }\r\n" + //
                                "}took " + elapsed + " ms, status=" + response.statusCode());
        if (response.statusCode() >= 400) {
            throw new RuntimeException("HTTP " + response.statusCode() + ": " + response.body());
        }
        return response.body();
    }

    private void delete(String url) throws Exception {
        long start = System.currentTimeMillis();
        HttpRequest request = authorizedBuilder(url).DELETE().build();
        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());
        long elapsed = System.currentTimeMillis() - start;
        System.out.println("[DELETE " + url + "] took " + elapsed + " ms, status=" + response.statusCode());
        if (response.statusCode() >= 400) {
            throw new RuntimeException("HTTP " + response.statusCode() + ": " + response.body());
        }
    }
}