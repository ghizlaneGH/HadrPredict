package com.ensao.hadrpredictapi.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Service
public class GeocodingService {
    @Value("${opencage.api.key}")
    private String apiKey;

    public double[] getCoordinates(String schoolName) {
        try {
            String query = URLEncoder.encode(schoolName + ", Morocco", StandardCharsets.UTF_8);
            String url = "https://api.opencagedata.com/geocode/v1/json?q=" + query + "&key=" + apiKey;

            System.out.println("API KEY = " + apiKey);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "Java App")
                    .build();

            //HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JSONObject json = new JSONObject(response.body());
            JSONArray results = json.getJSONArray("results");

            if (!results.isEmpty()) {
                JSONObject geometry = results.getJSONObject(0).getJSONObject("geometry");
                return new double[]{
                        geometry.getDouble("lat"),
                        geometry.getDouble("lng")
                };
            }
        } catch (Exception e) {
            System.out.println("Geocoding error: " + e.getMessage());
        }
        return new double[]{0.0, 0.0}; // default if not found
    }
}
