package com.example.pharma.service;

import com.example.pharma.dto.Location.CoordinateDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {

    @Value("${ors.apiKey}")
    private String apiKey;

    private final RestClient restClient = RestClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    public Geometry getRoadReachPolygon(double lat, double lon, double meters) {
        String url = "https://api.openrouteservice.org/v2/isochrones/driving-car";

        // ORS uses a POST request with a JSON body
        String requestBody = """
                {
                    "locations": [[%f, %f]],
                    "range_type": "distance",
                    "range": [%d]
                }
                """.formatted(lon, lat, (int) meters); // note: ORS takes lon,lat order
        String response;
        try {
            response = restClient.post()
                    .uri(url)
                    .header("Authorization", apiKey)
                    .header("Content-Type", "application/json")
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);
        } catch (Exception ex) {
            throw new RuntimeException("Routing service is currently unavailable, please try again later");
        }
        var polygon = parsePolygon(response);
        return polygon;
    }

    public List<Double> getRoadDistances(
            double userLat, double userLon,
            List<CoordinateDto> coordinates) { // each double[] is [lon, lat]

        if (coordinates.isEmpty()) return List.of();

        StringBuilder locations = new StringBuilder();
        locations.append("[%f, %f]".formatted(userLon, userLat));
        for (CoordinateDto coord : coordinates) {
            locations.append(", [%f, %f]".formatted(coord.longitude(), coord.latitude()));
        }

        String requestBody = """
            {
                "locations": [%s],
                "sources": [0],
                "metrics": ["distance"],
                "units": "km"
            }
            """.formatted(locations);

        String response;
        try {
            response = restClient.post()
                    .uri("https://api.openrouteservice.org/v2/matrix/driving-car")
                    .header("Authorization", apiKey)
                    .header("Content-Type", "application/json")
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            throw new RuntimeException("Matrix service unavailable: " + e.getMessage());
        }

        return parseDistances(response);
    }

    public Double getRoadDistance(double userLat, double userLon, CoordinateDto coordinate) {
        return getRoadDistances(userLat, userLon, List.of(coordinate)).get(0);
    }

    private List<Double> parseDistances(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode row = root.path("distances").get(0);

            List<Double> distances = new ArrayList<>();
            for (int i = 1; i < row.size(); i++) { // skip index 0 (user→user)
                distances.add(row.get(i).asDouble());
            }
            return distances;

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse matrix response", e);
        }
    }
    private Geometry parsePolygon(String geojson) {
        try {
            JsonNode root = objectMapper.readTree(geojson);
            JsonNode coordinates = root
                    .path("features")
                    .get(0)
                    .path("geometry")
                    .path("coordinates")
                    .get(0); // outer ring

            List<Coordinate> coords = new ArrayList<>();
            for (JsonNode point : coordinates) {
                double lon = point.get(0).asDouble();
                double lat = point.get(1).asDouble();
                coords.add(new Coordinate(lon, lat));
            }

            Polygon polygon = geometryFactory.createPolygon(coords.toArray(new Coordinate[0]));
            polygon.setSRID(4326);
            return polygon;

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse ORS isochrone response", e);
        }
    }
}