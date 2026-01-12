package com.carfuel.controller;

import com.carfuel.model.Car;
import com.carfuel.model.FuelEntry;
import com.carfuel.service.CarService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import static spark.Spark.*;
import java.util.Map;

public class CarController {
    private CarService carService;
    private ObjectMapper objectMapper;

    public CarController(CarService carService) {
        this.carService = carService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public void initRoutes() {
        post("/api/cars", (req, res) -> {
            res.type("application/json");
            try {
                // Parse JSON from request
                Map<String, Object> carData;
                try {
                    carData = objectMapper.readValue(
                        req.body(), 
                        new TypeReference<Map<String, Object>>() {}
                    );
                } catch (Exception e) {
                    res.status(400);
                    return "{\"error\":\"Invalid JSON format\"}";
                }
                
                String brand = (String) carData.get("brand");
                String model = (String) carData.get("model");
                Number yearNum = (Number) carData.get("year");
                int year = yearNum != null ? yearNum.intValue() : 0;

                if (brand == null || brand.trim().isEmpty()) {
                    res.status(400);
                    return "{\"error\":\"Missing or empty required field: brand\"}";
                }
                if (model == null || model.trim().isEmpty()) {
                    res.status(400);
                    return "{\"error\":\"Missing or empty required field: model\"}";
                }
                if (year == 0 || year < 1900 || year > 2100) {
                    res.status(400);
                    return "{\"error\":\"Invalid year: must be between 1900 and 2100\"}";
                }

                Car car = carService.createCar(brand.trim(), model.trim(), year);
                try {
                    return objectMapper.writeValueAsString(car);
                } catch (Exception jsonEx) {
                    res.status(500);
                    return "{\"error\":\"Internal server error: Failed to serialize response\"}";
                }
            } catch (IllegalArgumentException e) {
                res.status(400);
                try {
                    return objectMapper.writeValueAsString(Map.of("error", e.getMessage()));
                } catch (Exception jsonEx) {
                    return "{\"error\":\"Invalid request\"}";
                }
            } catch (Exception e) {
                res.status(500);
                return "{\"error\":\"Internal server error\"}";
            }
        });

        get("/api/cars", (req, res) -> {
            res.type("application/json");
            try {
                return objectMapper.writeValueAsString(carService.getAllCars());
            } catch (Exception e) {
                res.status(500);
                return "{\"error\":\"Internal server error\"}";
            }
        });

        get("/api/cars/:id", (req, res) -> {
            res.type("application/json");
            try {
                int carId = Integer.parseInt(req.params(":id"));
                Car car = carService.getCarById(carId);
                if (car == null) {
                    res.status(404);
                    return "{\"error\":\"Car not found with ID: " + carId + "\"}";
                }
                try {
                    return objectMapper.writeValueAsString(car);
                } catch (Exception jsonEx) {
                    res.status(500);
                    return "{\"error\":\"Internal server error\"}";
                }
            } catch (NumberFormatException e) {
                res.status(400);
                return "{\"error\":\"Invalid car ID format: " + req.params(":id") + "\"}";
            } catch (IllegalArgumentException e) {
                res.status(400);
                return "{\"error\":\"" + e.getMessage().replace("\"", "\\\"") + "\"}";
            } catch (Exception e) {
                res.status(500);
                return "{\"error\":\"Internal server error\"}";
            }
        });

        post("/api/cars/:id/fuel", (req, res) -> {
            res.type("application/json");
            try {
                int carId = Integer.parseInt(req.params(":id"));
                
                // Parse JSON body
                Map<String, Object> fuelData;
                try {
                    fuelData = objectMapper.readValue(
                        req.body(), 
                        new TypeReference<Map<String, Object>>() {}
                    );
                } catch (Exception e) {
                    res.status(400);
                    return "{\"error\":\"Invalid JSON format\"}";
                }

                Number litersNum = (Number) fuelData.get("liters");
                Number priceNum = (Number) fuelData.get("price");
                Number odometerNum = (Number) fuelData.get("odometer");

                if (litersNum == null || priceNum == null || odometerNum == null) {
                    res.status(400);
                    return "{\"error\":\"Missing required fields: liters, price, odometer\"}";
                }

                double liters = litersNum.doubleValue();
                double price = priceNum.doubleValue();
                double odometer = odometerNum.doubleValue();

                // Validate values
                if (liters <= 0) {
                    res.status(400);
                    return "{\"error\":\"Liters must be a positive number\"}";
                }
                if (price < 0) {
                    res.status(400);
                    return "{\"error\":\"Price cannot be negative\"}";
                }
                if (odometer < 0) {
                    res.status(400);
                    return "{\"error\":\"Odometer cannot be negative\"}";
                }

                FuelEntry fuelEntry = carService.addFuelEntry(carId, liters, price, odometer);

                if (fuelEntry == null) {
                    res.status(404);
                    return "{\"error\":\"Car not found with ID: " + carId + "\"}";
                }
                try {
                    return objectMapper.writeValueAsString(fuelEntry);
                } catch (Exception jsonEx) {
                    res.status(500);
                    return "{\"error\":\"Internal server error\"}";
                }
            } catch (NumberFormatException e) {
                res.status(400);
                return "{\"error\":\"Invalid car ID format: " + req.params(":id") + "\"}";
            } catch (IllegalArgumentException e) {
                res.status(400);
                return "{\"error\":\"" + e.getMessage().replace("\"", "\\\"") + "\"}";
            } catch (Exception e) {
                res.status(500);
                return "{\"error\":\"Internal server error\"}";
            }
        });

        get("/api/cars/:id/fuel/stats", (req, res) -> { 
            res.type("application/json");
            try {
                int carId = Integer.parseInt(req.params(":id"));
                Map<String, Object> stats = carService.calculateStats(carId);

                if (stats == null) {
                    res.status(404);
                    return "{\"error\":\"Car not found with ID: " + carId + "\"}";
                }
                try {
                    return objectMapper.writeValueAsString(stats);
                } catch (Exception jsonEx) {
                    res.status(500);
                    return "{\"error\":\"Internal server error\"}";
                }
            } catch (NumberFormatException e) {
                res.status(400);
                return "{\"error\":\"Invalid car ID format: " + req.params(":id") + "\"}";
            } catch (IllegalArgumentException e) {
                res.status(400);
                return "{\"error\":\"" + e.getMessage().replace("\"", "\\\"") + "\"}";
            } catch (Exception e) {
                res.status(500);
                return "{\"error\":\"Internal server error\"}";
            }
        });

        delete("/api/cars/:id", (req, res) -> {
            res.type("application/json");
            try {
                int carId = Integer.parseInt(req.params(":id"));
                boolean deleted = carService.deleteCar(carId);
                if (!deleted) {
                    res.status(404);
                    return "{\"error\":\"Car not found with ID: " + carId + "\"}";
                }
                return "{\"message\":\"Car deleted successfully\"}";
            } catch (NumberFormatException e) {
                res.status(400);
                return "{\"error\":\"Invalid car ID format: " + req.params(":id") + "\"}";
            } catch (IllegalArgumentException e) {
                res.status(400);
                return "{\"error\":\"" + e.getMessage().replace("\"", "\\\"") + "\"}";
            } catch (Exception e) {
                res.status(500);
                return "{\"error\":\"Internal server error\"}";
            }
        });

        exception(NumberFormatException.class, (e, req, res) -> {
            res.type("application/json");
            res.status(400);
            try {
                res.body(objectMapper.writeValueAsString(Map.of("error", "Invalid number format: " + e.getMessage())));
            } catch (Exception ex) {
                res.body("{\"error\":\"Invalid number format\"}");
            }
        });

        exception(IllegalArgumentException.class, (e, req, res) -> {
            res.type("application/json");
            res.status(400);
            try {
                res.body(objectMapper.writeValueAsString(Map.of("error", e.getMessage())));
            } catch (Exception ex) {
                res.body("{\"error\":\"Invalid request\"}");
            }
        });

        exception(Exception.class, (e, req, res) -> {
            res.type("application/json");
            res.status(500);
            try {
                res.body(objectMapper.writeValueAsString(Map.of("error", "Internal server error")));
            } catch (Exception ex) {
                res.body("{\"error\":\"Internal server error\"}");
            }
        });
    }
}
