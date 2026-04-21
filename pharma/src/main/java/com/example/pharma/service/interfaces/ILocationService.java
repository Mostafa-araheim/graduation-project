package com.example.pharma.service.interfaces;

import com.example.pharma.dto.Location.CoordinateDto;
import org.locationtech.jts.geom.Geometry;

import java.util.List;

public interface ILocationService {
    Geometry getRoadReachPolygon(double lat, double lon, double meters);
    List<Double> getRoadDistances(double userLat, double userLon, List<CoordinateDto> coordinates);
    Double getRoadDistance(double userLat, double userLon, CoordinateDto coordinate);
}
