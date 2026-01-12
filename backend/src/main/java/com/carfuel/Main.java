package com.carfuel;

import com.carfuel.controller.CarController;
import com.carfuel.service.CarService;
import com.carfuel.servlet.FuelStatsServlet;
import spark.Request;
import spark.Response;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

import static spark.Spark.*;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        // throw new RuntimeException("Error: Server cannot start!");
        
        port(8080); 

        // Enable CORS
        options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }
            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
        });
        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
        });

        // Create shared service instance
        CarService carService = new CarService();
        
        // Setup routes
        CarController carController = new CarController(carService);
        carController.initRoutes();

        // Setup servlet route (Part 2) - uses same service instance
        FuelStatsServlet servlet = new FuelStatsServlet(carService);
        get("/servlet/fuel-stats", (Request request, Response response) -> {
            HttpServletRequest httpRequest = request.raw();
            HttpServletResponse httpResponse = response.raw();
            try {
                servlet.handleGet(httpRequest, httpResponse);
            } catch (Exception e) {
                logger.severe("Error in servlet: " + e.getMessage());
                httpResponse.setStatus(500);
            }
            return "";
        });

        logger.info("My Fuel Tracker API Server is up and running on port 8080!");
    }
}
