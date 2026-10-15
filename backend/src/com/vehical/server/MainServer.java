package com.vehical.server;

import com.vehical.dao.UserDAO;
import com.vehical.dao.VehicleDAO;
import com.vehical.dao.PUCCertificateDAO;
import com.vehical.dao.ChallanDAO;
import com.vehical.dao.AdminDAO;
import com.vehical.model.User;
import com.vehical.model.Vehicle;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.sql.SQLException;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MainServer {

    private static final int PORT = 8081;
    private static com.sun.net.httpserver.HttpServer httpServer;

    public static void main(String[] args) throws IOException {
        httpServer = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(PORT), 0);
        
        // Register API endpoints
        httpServer.createContext("/api/auth/login", new LoginHandler());
        httpServer.createContext("/api/auth/me", new MeHandler());
        httpServer.createContext("/api/vehicle/lookup", new VehicleLookupHandler());
        httpServer.createContext("/api/vehicle/register", new VehicleRegisterHandler());
        httpServer.createContext("/puc/issue", new PUCIssueHandler());
        httpServer.createContext("/puc/history", new PUCHistoryHandler());
        httpServer.createContext("/api/challan/pay", new ChallanPayHandler());
        httpServer.createContext("/api/challan/vehicle", new ChallanVehicleHandler());
        
        // Admin API endpoints
        httpServer.createContext("/api/admin/users", new AdminUsersHandler());
        httpServer.createContext("/api/admin/vehicles", new AdminVehiclesHandler());
        httpServer.createContext("/api/admin/challans", new AdminChallansHandler());
        httpServer.createContext("/api/admin/puc", new AdminPUCHandler());
        httpServer.createContext("/api/admin/owners", new AdminOwnersHandler());

        // Serve static files from frontend directory
        String frontendPath = "../frontend";
        if (args.length > 0) {
            frontendPath = args[0];
        }
        httpServer.createContext("/", new StaticFileHandler(frontendPath));

        // Enable CORS
        httpServer.setExecutor(null);

        System.out.println("Server started on port " + PORT);
        System.out.println("Frontend: http://localhost:" + PORT);
        System.out.println("API Endpoints:");
        System.out.println("  POST http://localhost:" + PORT + "/api/auth/login");
        System.out.println("  GET  http://localhost:" + PORT + "/api/auth/me");
        System.out.println("  GET  http://localhost:" + PORT + "/api/vehicle/lookup?vehicleNo=MH-04-AB-1234");
        System.out.println("  POST http://localhost:" + PORT + "/api/vehicle/register");
        System.out.println("  POST http://localhost:" + PORT + "/puc/issue");
        System.out.println("  GET  http://localhost:" + PORT + "/puc/history");
        System.out.println("  POST http://localhost:" + PORT + "/api/challan/pay");
        System.out.println("  GET  http://localhost:" + PORT + "/api/challan/vehicle?vehicleNo=MH-04-AB-1234");
        System.out.println("  GET  http://localhost:" + PORT + "/api/admin/users");
        System.out.println("  GET  http://localhost:" + PORT + "/api/admin/vehicles");
        System.out.println("  GET  http://localhost:" + PORT + "/api/admin/challans");
        System.out.println("  GET  http://localhost:" + PORT + "/api/admin/puc");
        System.out.println("  GET  http://localhost:" + PORT + "/api/admin/owners");
        System.out.println("Admin Panel: http://localhost:" + PORT + "/admin-panel.html");

        httpServer.start();
    }

    static class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equals("POST")) {
                sendResponse(exchange, 405, "{\"success\": false, \"message\": \"Method not allowed\"}");
                return;
            }

            // Handle CORS
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "POST, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
            
            if (exchange.getRequestMethod().equals("OPTIONS")) {
                exchange.sendResponseHeaders(200, -1);
                return;
            }

            try {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Map<String, String> params = parseJson(body);
                
                String username = params.get("username");
                String password = params.get("password");
                String role = params.get("role");
                String vehicleNumber = params.get("vehicleNumber");
                String chassisLast4 = params.get("chassisLast4");

                // ===== CITIZEN LOGIN (vehicle number + last 4 of chassis) =====
                if (vehicleNumber != null && !vehicleNumber.trim().isEmpty()) {
                    handleCitizenLogin(exchange, vehicleNumber, chassisLast4, role);
                    return;
                }

                if (username == null || password == null) {
                    sendResponse(exchange, 400, "{\"success\": false, \"message\": \"Username and password required\"}");
                    return;
                }

                UserDAO userDAO = new UserDAO();
                User user = userDAO.findByUsername(username);

                if (user == null) {
                    sendResponse(exchange, 401, "{\"success\": false, \"message\": \"Invalid credentials\"}");
                    return;
                }

                if (!user.getIsActive()) {
                    sendResponse(exchange, 403, "{\"success\": false, \"message\": \"Account is inactive\"}");
                    return;
                }

                // Simple password check (in production, use bcrypt)
                if (!password.equals("password123") && !password.equals("admin123")) {
                    // For demo, accept 4+ character passwords
                    if (password.length() < 4) {
                        sendResponse(exchange, 401, "{\"success\": false, \"message\": \"Invalid credentials\"}");
                        return;
                    }
                }

                // Check role if provided
                if (role != null && !role.isEmpty() && !role.equals(user.getRole())) {
                    sendResponse(exchange, 403, "{\"success\": false, \"message\": \"Role mismatch\"}");
                    return;
                }

                // Generate simple token
                String token = "token_" + System.currentTimeMillis() + "_" + user.getId();

                String responseData = String.format(
                    "{\"success\": true, \"message\": \"Login successful\", \"data\": {" +
                    "\"token\": \"%s\", \"tokenType\": \"Bearer\", \"expiresIn\": 86400, " +
                    "\"userId\": %d, \"username\": \"%s\", \"fullName\": \"%s\", \"email\": \"%s\", " +
                    "\"phone\": \"%s\", \"role\": \"%s\", \"department\": \"%s\", " +
                    "\"centerName\": \"%s\", \"agencyName\": \"%s\", \"licenseNumber\": \"%s\", " +
                    "\"loginTime\": \"%s\"}}",
                    token, user.getId(), user.getUsername(), user.getFullName(),
                    user.getEmail(), user.getPhone(), user.getRole(),
                    user.getDepartment() != null ? user.getDepartment() : "",
                    user.getCenterName() != null ? user.getCenterName() : "",
                    user.getAgencyName() != null ? user.getAgencyName() : "",
                    user.getLicenseNumber() != null ? user.getLicenseNumber() : "",
                    java.time.Instant.now().toString()
                );

                sendResponse(exchange, 200, responseData);

            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(exchange, 500, "{\"success\": false, \"message\": \"Server error: " + e.getMessage() + "\"}");
            }
        }
    }

    /**
     * Citizen login: authenticate by vehicle number + last 4 digits of chassis number.
     */
    static void handleCitizenLogin(HttpExchange exchange, String vehicleNumber, String chassisLast4, String role) throws IOException {
        if (chassisLast4 == null || chassisLast4.trim().isEmpty()) {
            sendResponse(exchange, 400, "{\"success\": false, \"message\": \"Last 4 digits of chassis number are required.\"}");
            return;
        }

        if (role != null && !role.isEmpty() && !role.equals("CITIZEN")) {
            sendResponse(exchange, 403, "{\"success\": false, \"message\": \"Role mismatch. Vehicle login is for citizens only.\"}");
            return;
        }

        try {
            VehicleDAO vehicleDAO = new VehicleDAO();
            Vehicle vehicle = vehicleDAO.findByVehicleNo(vehicleNumber);

            if (vehicle == null) {
                sendResponse(exchange, 401, "{\"success\": false, \"message\": \"Vehicle not found in database. Please check your vehicle number.\"}");
                return;
            }

            // Normalize chassis: keep alphanumeric, uppercase, take last 4
            String vehicleChassis = vehicle.getChassisNumber() == null ? "" : vehicle.getChassisNumber().replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
            String inputChassis = chassisLast4.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
            String vehicleLast4 = vehicleChassis.length() >= 4 ? vehicleChassis.substring(vehicleChassis.length() - 4) : vehicleChassis;
            String inputLast4 = inputChassis.length() >= 4 ? inputChassis.substring(inputChassis.length() - 4) : inputChassis;

            if (vehicleLast4.isEmpty() || !vehicleLast4.equals(inputLast4)) {
                sendResponse(exchange, 401, "{\"success\": false, \"message\": \"Chassis number does not match our records for this vehicle.\"}");
                return;
            }

            String ownerName = vehicle.getOwnerName() != null ? vehicle.getOwnerName() : "Citizen";
            String ownerEmail = vehicle.getOwnerEmail() != null ? vehicle.getOwnerEmail() : "";
            String ownerPhone = vehicle.getOwnerMobile() != null ? vehicle.getOwnerMobile() : "";
            String normalizedVehicle = vehicleNumber.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
            long userId = vehicle.getOwnerId() != null ? vehicle.getOwnerId() : (System.currentTimeMillis() % 100000);

            String token = "token_" + System.currentTimeMillis() + "_" + userId;

            String responseData = String.format(
                "{\"success\": true, \"message\": \"Login successful\", \"data\": {" +
                "\"token\": \"%s\", \"tokenType\": \"Bearer\", \"expiresIn\": 86400, " +
                "\"userId\": %d, \"username\": \"%s\", \"fullName\": \"%s\", \"email\": \"%s\", " +
                "\"phone\": \"%s\", \"role\": \"CITIZEN\", \"vehicleNumber\": \"%s\", " +
                "\"department\": \"\", \"centerName\": \"\", \"agencyName\": \"\", \"licenseNumber\": \"\", " +
                "\"loginTime\": \"%s\"}}",
                token, userId, normalizedVehicle, escapeJson(ownerName), escapeJson(ownerEmail),
                escapeJson(ownerPhone), normalizedVehicle, java.time.Instant.now().toString()
            );

            sendResponse(exchange, 200, responseData);
        } catch (SQLException e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"success\": false, \"message\": \"Database error: " + e.getMessage() + "\"}");
        }
    }

    private static String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }

    static class MeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equals("GET")) {
                sendResponse(exchange, 405, "{\"success\": false, \"message\": \"Method not allowed\"}");
                return;
            }

            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            
            try {
                String query = exchange.getRequestURI().getQuery();
                if (query == null) {
                    sendResponse(exchange, 400, "{\"success\": false, \"message\": \"User ID required\"}");
                    return;
                }

                Map<String, String> params = parseQuery(query);
                String userIdStr = params.get("userId");
                
                if (userIdStr == null) {
                    sendResponse(exchange, 400, "{\"success\": false, \"message\": \"User ID required\"}");
                    return;
                }

                Long userId = Long.parseLong(userIdStr);
                UserDAO userDAO = new UserDAO();
                User user = userDAO.findById(userId);

                if (user == null) {
                    sendResponse(exchange, 404, "{\"success\": false, \"message\": \"User not found\"}");
                    return;
                }

                String responseData = String.format(
                    "{\"success\": true, \"data\": {" +
                    "\"userId\": %d, \"username\": \"%s\", \"fullName\": \"%s\", \"email\": \"%s\", " +
                    "\"phone\": \"%s\", \"role\": \"%s\", \"department\": \"%s\", " +
                    "\"centerName\": \"%s\", \"agencyName\": \"%s\", \"licenseNumber\": \"%s\"}}",
                    user.getId(), user.getUsername(), user.getFullName(),
                    user.getEmail(), user.getPhone(), user.getRole(),
                    user.getDepartment() != null ? user.getDepartment() : "",
                    user.getCenterName() != null ? user.getCenterName() : "",
                    user.getAgencyName() != null ? user.getAgencyName() : "",
                    user.getLicenseNumber() != null ? user.getLicenseNumber() : ""
                );

                sendResponse(exchange, 200, responseData);

            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(exchange, 500, "{\"success\": false, \"message\": \"Server error\"}");
            }
        }
    }

    static class VehicleLookupHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equals("GET")) {
                sendResponse(exchange, 405, "{\"success\": false, \"message\": \"Method not allowed\"}");
                return;
            }

            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");

            try {
                String query = exchange.getRequestURI().getQuery();
                if (query == null) {
                    sendResponse(exchange, 400, "{\"success\": false, \"message\": \"Vehicle number is required\"}");
                    return;
                }

                Map<String, String> params = parseQuery(query);
                String vehicleNo = params.get("vehicleNo");

                if (vehicleNo == null || vehicleNo.trim().isEmpty()) {
                    sendResponse(exchange, 400, "{\"success\": false, \"message\": \"Vehicle number is required\"}");
                    return;
                }

                // Normalize vehicle number
                String normalizedPlate = vehicleNo
                        .trim()
                        .toUpperCase()
                        .replaceAll("[^A-Z0-9]", "");

                System.out.println("[VehicleLookup] Received: " + vehicleNo + " -> normalized: " + normalizedPlate);

                VehicleDAO vehicleDAO = new VehicleDAO();
                Vehicle vehicle = vehicleDAO.findByVehicleNo(normalizedPlate);

                if (vehicle == null) {
                    System.out.println("[VehicleLookup] Vehicle not found in database for: " + normalizedPlate);
                    sendResponse(exchange, 404, "{\"success\": false, \"message\": \"Vehicle not found in database\"}");
                    return;
                }

                System.out.println("[VehicleLookup] Vehicle found: " + vehicle.getVehicleNo());

                String responseData = String.format(
                    "{\"success\": true, \"message\": \"Vehicle found\", \"vehicle\": {" +
                    "\"vehicleNo\": \"%s\", \"ownerId\": %d, \"vehicleType\": \"%s\", \"make\": \"%s\", \"model\": \"%s\", " +
                    "\"fuelType\": \"%s\", \"registrationDate\": \"%s\", \"registrationState\": \"%s\", " +
                    "\"insuranceStatus\": \"%s\", \"pucStatus\": \"%s\", \"rcStatus\": \"%s\", " +
                    "\"engineNumber\": \"%s\", \"chassisNumber\": \"%s\", \"blacklisted\": %b, " +
                    "\"ownerName\": \"%s\", \"ownerMobile\": \"%s\", \"ownerEmail\": \"%s\"}}",
                    vehicle.getVehicleNo(), vehicle.getOwnerId(), vehicle.getVehicleType(), vehicle.getMake(), vehicle.getModel(),
                    vehicle.getFuelType(), vehicle.getRegistrationDate(), vehicle.getRegistrationState(),
                    vehicle.getInsuranceStatus(), vehicle.getPucStatus(), vehicle.getRcStatus(),
                    vehicle.getEngineNumber(), vehicle.getChassisNumber(), vehicle.getBlacklisted(),
                    vehicle.getOwnerName() != null ? vehicle.getOwnerName() : "",
                    vehicle.getOwnerMobile() != null ? vehicle.getOwnerMobile() : "",
                    vehicle.getOwnerEmail() != null ? vehicle.getOwnerEmail() : ""
                );

                sendResponse(exchange, 200, responseData);

            } catch (SQLException e) {
                System.err.println("Vehicle lookup database error: " + e.getMessage());
                e.printStackTrace();
                sendResponse(exchange, 500, "{\"success\": false, \"message\": \"Database error: " + e.getMessage() + "\"}");
            } catch (Exception e) {
                System.err.println("Vehicle lookup error: " + e.getMessage());
                e.printStackTrace();
                sendResponse(exchange, 500, "{\"success\": false, \"message\": \"Server error: " + e.getMessage() + "\"}");
            }
        }
    }

    static class VehicleRegisterHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equals("POST")) {
                sendResponse(exchange, 405, "{\"success\": false, \"message\": \"Method not allowed\"}");
                return;
            }

            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "POST, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
            
            if (exchange.getRequestMethod().equals("OPTIONS")) {
                exchange.sendResponseHeaders(200, -1);
                return;
            }

            try {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Map<String, String> params = parseJson(body);
                
                String vehicleNo = params.get("vehicleNo");
                String ownerName = params.get("ownerName");
                String ownerMobile = params.get("ownerMobile");
                String ownerEmail = params.get("ownerEmail");
                String ownerAddress = params.get("ownerAddress");
                String vehicleType = params.get("vehicleType");
                String make = params.get("make");
                String model = params.get("model");
                String fuelType = params.get("fuelType");
                String registrationDate = params.get("registrationDate");
                String registrationState = params.get("registrationState");
                String engineNumber = params.get("engineNumber");
                String chassisNumber = params.get("chassisNumber");

                if (vehicleNo == null || vehicleNo.isEmpty() || ownerName == null || ownerName.isEmpty()) {
                    sendResponse(exchange, 400, "{\"success\": false, \"message\": \"Vehicle number and owner name are required\"}");
                    return;
                }

                VehicleDAO vehicleDAO = new VehicleDAO();
                
                // Check if vehicle already exists
                Vehicle existingVehicle = vehicleDAO.findByVehicleNo(vehicleNo);
                if (existingVehicle != null) {
                    sendResponse(exchange, 409, "{\"success\": false, \"message\": \"Vehicle already registered\"}");
                    return;
                }

                // Insert owner and get owner_id
                Long ownerId = vehicleDAO.insertOwner(
                    ownerName,
                    ownerMobile != null ? ownerMobile : "",
                    ownerEmail != null ? ownerEmail : "",
                    ownerAddress != null ? ownerAddress : ""
                );

                if (ownerId == null) {
                    sendResponse(exchange, 500, "{\"success\": false, \"message\": \"Failed to create owner\"}");
                    return;
                }

                // Create vehicle object
                Vehicle vehicle = new Vehicle();
                vehicle.setVehicleNo(vehicleNo);
                vehicle.setOwnerId(ownerId);
                vehicle.setVehicleType(vehicleType != null ? vehicleType : "Two Wheeler");
                vehicle.setMake(make != null ? make : "");
                vehicle.setModel(model != null ? model : "");
                vehicle.setFuelType(fuelType != null ? fuelType : "Petrol");
                vehicle.setRegistrationDate(registrationDate != null ? registrationDate : java.time.LocalDate.now().toString());
                vehicle.setRegistrationState(registrationState != null ? registrationState : "Maharashtra");
                vehicle.setInsuranceStatus("Valid");
                vehicle.setPucStatus("Valid");
                vehicle.setRcStatus("Active");
                vehicle.setEngineNumber(engineNumber != null ? engineNumber : "");
                vehicle.setChassisNumber(chassisNumber != null ? chassisNumber : "");
                vehicle.setBlacklisted(false);

                // Insert vehicle
                boolean success = vehicleDAO.insertVehicle(vehicle);

                if (success) {
                    String responseData = String.format(
                        "{\"success\": true, \"message\": \"Vehicle registered successfully\", \"data\": {" +
                        "\"vehicleNo\": \"%s\", \"ownerId\": %d, \"ownerName\": \"%s\"}}",
                        vehicleNo, ownerId, ownerName
                    );
                    sendResponse(exchange, 201, responseData);
                } else {
                    sendResponse(exchange, 500, "{\"success\": false, \"message\": \"Failed to register vehicle\"}");
                }

            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(exchange, 500, "{\"success\": false, \"message\": \"Server error: " + e.getMessage() + "\"}");
            }
        }
    }

    static class PUCIssueHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equals("POST")) {
                sendResponse(exchange, 405, "{\"success\": false, \"message\": \"Method not allowed\"}");
                return;
            }

            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "POST, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

            if (exchange.getRequestMethod().equals("OPTIONS")) {
                exchange.sendResponseHeaders(200, -1);
                return;
            }

            try {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Map<String, String> params = parseJson(body);

                String certificateNumber = params.get("certificateNumber");
                String vehicleNo = params.get("vehicleNo");
                String ownerName = params.get("ownerName");
                String vehicleType = params.get("vehicleType");
                String fuelType = params.get("fuelType");
                String registrationDate = params.get("registrationDate");
                String engineNumber = params.get("engineNumber");
                String chassisNumber = params.get("chassisNumber");
                String centerId = params.get("centerId");
                String centerName = params.get("centerName");
                String testDateTime = params.get("testDateTime");
                String emissionNorms = params.get("emissionNorms");
                String odometerKmStr = params.get("odometerKm");
                String coPercentStr = params.get("coPercent");
                String hcPpmStr = params.get("hcPpm");
                String co2PercentStr = params.get("co2Percent");
                String o2PercentStr = params.get("o2Percent");
                String lambdaValueStr = params.get("lambdaValue");
                String smokeDensityStr = params.get("smokeDensity");
                String rpmStr = params.get("rpm");
                String temperatureStr = params.get("temperature");
                String pollutionUnderControl = params.get("pollutionUnderControl");
                String passStatus = params.get("passStatus");
                String validFrom = params.get("validFrom");
                String validUpto = params.get("validUpto");
                String issuedByUserIdStr = params.get("issuedByUserId");
                String issuedByName = params.get("issuedByName");

                if (certificateNumber == null || vehicleNo == null) {
                    sendResponse(exchange, 400, "{\"success\": false, \"message\": \"Certificate number and vehicle number are required\"}");
                    return;
                }

                // Parse numeric values
                Double odometerKm = odometerKmStr != null && !odometerKmStr.isEmpty() ? Double.parseDouble(odometerKmStr) : null;
                Double coPercent = coPercentStr != null && !coPercentStr.isEmpty() ? Double.parseDouble(coPercentStr) : null;
                Double hcPpm = hcPpmStr != null && !hcPpmStr.isEmpty() ? Double.parseDouble(hcPpmStr) : null;
                Double co2Percent = co2PercentStr != null && !co2PercentStr.isEmpty() ? Double.parseDouble(co2PercentStr) : null;
                Double o2Percent = o2PercentStr != null && !o2PercentStr.isEmpty() ? Double.parseDouble(o2PercentStr) : null;
                Double lambdaValue = lambdaValueStr != null && !lambdaValueStr.isEmpty() ? Double.parseDouble(lambdaValueStr) : null;
                Double smokeDensity = smokeDensityStr != null && !smokeDensityStr.isEmpty() ? Double.parseDouble(smokeDensityStr) : null;
                Double rpm = rpmStr != null && !rpmStr.isEmpty() ? Double.parseDouble(rpmStr) : null;
                Double temperature = temperatureStr != null && !temperatureStr.isEmpty() ? Double.parseDouble(temperatureStr) : null;
                Long issuedByUserId = issuedByUserIdStr != null && !issuedByUserIdStr.isEmpty() ? Long.parseLong(issuedByUserIdStr) : null;

                // Insert PUC certificate
                boolean success = PUCCertificateDAO.insertPUCCertificate(
                    certificateNumber, vehicleNo, ownerName, vehicleType, fuelType,
                    registrationDate, engineNumber, chassisNumber, centerId, centerName,
                    testDateTime, emissionNorms, odometerKm, coPercent, hcPpm, co2Percent,
                    o2Percent, lambdaValue, smokeDensity, rpm, temperature,
                    pollutionUnderControl, passStatus, validFrom, validUpto,
                    issuedByUserId, issuedByName
                );

                if (success) {
                    // Update vehicle PUC status
                    String newPUCStatus = "Yes".equalsIgnoreCase(pollutionUnderControl) ? "Valid" : "Expired";
                    PUCCertificateDAO.updateVehiclePUCStatus(vehicleNo, newPUCStatus);

                    String responseData = String.format(
                        "{\"success\": true, \"message\": \"PUC Certificate issued successfully\", \"data\": {" +
                        "\"certificateNumber\": \"%s\", \"vehicleNo\": \"%s\", \"status\": \"%s\"}}",
                        certificateNumber, vehicleNo, newPUCStatus
                    );
                    sendResponse(exchange, 201, responseData);
                } else {
                    sendResponse(exchange, 500, "{\"success\": false, \"message\": \"Failed to issue PUC certificate\"}");
                }

            } catch (NumberFormatException e) {
                System.err.println("PUC issue error - invalid number format: " + e.getMessage());
                e.printStackTrace();
                sendResponse(exchange, 400, "{\"success\": false, \"message\": \"Invalid numeric value in request\"}");
            } catch (Exception e) {
                System.err.println("PUC issue error: " + e.getMessage());
                e.printStackTrace();
                sendResponse(exchange, 500, "{\"success\": false, \"message\": \"Server error: " + e.getMessage() + "\"}");
            }
        }
    }

    static class ChallanPayHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equals("POST")) {
                sendResponse(exchange, 405, "{\"success\": false, \"message\": \"Method not allowed\"}");
                return;
            }

            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "POST, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

            if (exchange.getRequestMethod().equals("OPTIONS")) {
                exchange.sendResponseHeaders(200, -1);
                return;
            }

            try {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Map<String, String> params = parseJson(body);

                String challanNumber = params.get("challanNumber");
                String vehicleNumber = params.get("vehicleNumber");

                if (challanNumber == null || challanNumber.isEmpty()) {
                    sendResponse(exchange, 400, "{\"success\": false, \"message\": \"Challan number is required\"}");
                    return;
                }

                ChallanDAO challanDAO = new ChallanDAO();
                
                // Generate payment reference
                String paymentReference = "PAY" + System.currentTimeMillis();
                
                // Update challan status in database
                boolean success = challanDAO.updateChallanStatus(challanNumber, "PAID", paymentReference);

                if (success) {
                    String responseData = String.format(
                        "{\"success\": true, \"message\": \"Challan paid successfully\", \"data\": {" +
                        "\"challanNumber\": \"%s\", \"paymentReference\": \"%s\", \"paymentDate\": \"%s\"}}",
                        challanNumber, paymentReference, new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date())
                    );
                    sendResponse(exchange, 200, responseData);
                } else {
                    sendResponse(exchange, 500, "{\"success\": false, \"message\": \"Failed to update challan status in database\"}");
                }

            } catch (Exception e) {
                System.err.println("Challan payment error: " + e.getMessage());
                e.printStackTrace();
                sendResponse(exchange, 500, "{\"success\": false, \"message\": \"Server error: " + e.getMessage() + "\"}");
            }
        }
    }

    static class ChallanVehicleHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equals("GET")) {
                sendResponse(exchange, 405, "{\"success\": false, \"message\": \"Method not allowed\"}");
                return;
            }

            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");

            try {
                String query = exchange.getRequestURI().getQuery();
                if (query == null) {
                    sendResponse(exchange, 400, "{\"success\": false, \"message\": \"Vehicle number is required\"}");
                    return;
                }

                Map<String, String> params = parseQuery(query);
                String vehicleNo = params.get("vehicleNo");

                if (vehicleNo == null || vehicleNo.trim().isEmpty()) {
                    sendResponse(exchange, 400, "{\"success\": false, \"message\": \"Vehicle number is required\"}");
                    return;
                }

                ChallanDAO challanDAO = new ChallanDAO();
                java.util.List<java.util.Map<String, Object>> challans = challanDAO.getChallansByVehicleNo(vehicleNo);

                StringBuilder json = new StringBuilder();
                json.append("{\"success\": true, \"message\": \"Challans retrieved\", \"data\": [");

                for (int i = 0; i < challans.size(); i++) {
                    if (i > 0) json.append(",");
                    java.util.Map<String, Object> challan = challans.get(i);
                    json.append("{");
                    json.append("\"challanId\":").append(challan.get("challanId")).append(",");
                    json.append("\"challanNumber\":\"").append(challan.get("challanNumber")).append("\",");
                    json.append("\"vehicleNo\":\"").append(challan.get("vehicleNo")).append("\",");
                    json.append("\"officerId\":\"").append(challan.get("officerId")).append("\",");
                    json.append("\"violationIds\":\"").append(challan.get("violationIds")).append("\",");
                    json.append("\"violationName\":\"").append(challan.get("violationName")).append("\",");
                    json.append("\"totalFineAmount\":").append(challan.get("totalFineAmount")).append(",");
                    json.append("\"challanDate\":\"").append(challan.get("challanDate")).append("\",");
                    json.append("\"challanTime\":\"").append(challan.get("challanTime")).append("\",");
                    json.append("\"location\":\"").append(challan.get("location") != null ? challan.get("location") : "").append("\",");
                    json.append("\"status\":\"").append(challan.get("status")).append("\",");
                    json.append("\"paymentStatus\":\"").append(challan.get("paymentStatus")).append("\",");
                    json.append("\"remarks\":\"").append(challan.get("remarks") != null ? challan.get("remarks") : "").append("\",");
                    json.append("\"paymentDate\":\"").append(challan.get("paymentDate") != null ? challan.get("paymentDate") : "").append("\",");
                    json.append("\"paymentReference\":\"").append(challan.get("paymentReference") != null ? challan.get("paymentReference") : "").append("\"");
                    json.append("}");
                }

                json.append("]}");
                sendResponse(exchange, 200, json.toString());

            } catch (Exception e) {
                System.err.println("Challan vehicle error: " + e.getMessage());
                e.printStackTrace();
                sendResponse(exchange, 500, "{\"success\": false, \"message\": \"Server error: " + e.getMessage() + "\"}");
            }
        }
    }

    static class PUCHistoryHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equals("GET")) {
                sendResponse(exchange, 405, "{\"success\": false, \"message\": \"Method not allowed\"}");
                return;
            }

            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");

            try {
                java.util.List<java.util.Map<String, Object>> certificates = PUCCertificateDAO.getAllPUCCertificates();

                StringBuilder json = new StringBuilder();
                json.append("{\"success\": true, \"message\": \"PUC certificates retrieved\", \"data\": [");

                for (int i = 0; i < certificates.size(); i++) {
                    if (i > 0) json.append(",");
                    java.util.Map<String, Object> cert = certificates.get(i);
                    json.append("{");
                    json.append("\"pucId\":").append(cert.get("pucId")).append(",");
                    json.append("\"certificateNumber\":\"").append(cert.get("certificateNumber")).append("\",");
                    json.append("\"vehicleNo\":\"").append(cert.get("vehicleNo")).append("\",");
                    json.append("\"ownerName\":\"").append(cert.get("ownerName")).append("\",");
                    json.append("\"vehicleType\":\"").append(cert.get("vehicleType")).append("\",");
                    json.append("\"fuelType\":\"").append(cert.get("fuelType")).append("\",");
                    json.append("\"centerName\":\"").append(cert.get("centerName")).append("\",");
                    json.append("\"testDateTime\":\"").append(cert.get("testDateTime")).append("\",");
                    json.append("\"validFrom\":\"").append(cert.get("validFrom")).append("\",");
                    json.append("\"validUpto\":\"").append(cert.get("validUpto")).append("\",");
                    json.append("\"status\":\"").append(cert.get("status")).append("\",");
                    json.append("\"coPercent\":").append(cert.get("coPercent")).append(",");
                    json.append("\"hcPpm\":").append(cert.get("hcPpm")).append(",");
                    json.append("\"co2Percent\":").append(cert.get("co2Percent")).append(",");
                    json.append("\"lambdaValue\":").append(cert.get("lambdaValue")).append(",");
                    json.append("\"smokeDensity\":").append(cert.get("smokeDensity")).append(",");
                    json.append("\"odometerKm\":").append(cert.get("odometerKm")).append(",");
                    json.append("\"issuedByName\":\"").append(cert.get("issuedByName")).append("\",");
                    json.append("\"createdAt\":\"").append(cert.get("createdAt")).append("\"");
                    json.append("}");
                }

                json.append("]}");
                sendResponse(exchange, 200, json.toString());

            } catch (Exception e) {
                System.err.println("PUC history error: " + e.getMessage());
                e.printStackTrace();
                sendResponse(exchange, 500, "{\"success\": false, \"message\": \"Server error: " + e.getMessage() + "\"}");
            }
        }
    }

    // ==================== ADMIN API HANDLERS ====================

    static class AdminUsersHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
            
            if (exchange.getRequestMethod().equals("OPTIONS")) {
                exchange.sendResponseHeaders(200, -1);
                return;
            }
            
            AdminDAO adminDAO = new AdminDAO();
            
            try {
                String method = exchange.getRequestMethod();
                
                if (method.equals("GET")) {
                    java.util.List<java.util.Map<String, Object>> users = adminDAO.getAllUsers();
                    
                    StringBuilder json = new StringBuilder();
                    json.append("{\"success\": true, \"message\": \"Users retrieved\", \"data\": [");
                    
                    for (int i = 0; i < users.size(); i++) {
                        if (i > 0) json.append(",");
                        java.util.Map<String, Object> user = users.get(i);
                        json.append("{");
                        json.append("\"id\":").append(user.get("id")).append(",");
                        json.append("\"username\":\"").append(user.get("username")).append("\",");
                        json.append("\"fullName\":\"").append(user.get("fullName")).append("\",");
                        json.append("\"email\":\"").append(user.get("email")).append("\",");
                        json.append("\"phone\":\"").append(user.get("phone")).append("\",");
                        json.append("\"role\":\"").append(user.get("role")).append("\",");
                        json.append("\"department\":\"").append(user.get("department") != null ? user.get("department") : "").append("\",");
                        json.append("\"centerName\":\"").append(user.get("centerName") != null ? user.get("centerName") : "").append("\",");
                        json.append("\"agencyName\":\"").append(user.get("agencyName") != null ? user.get("agencyName") : "").append("\",");
                        json.append("\"licenseNumber\":\"").append(user.get("licenseNumber") != null ? user.get("licenseNumber") : "").append("\",");
                        json.append("\"isActive\":").append(user.get("isActive"));
                        json.append("}");
                    }
                    
                    json.append("]}");
                    sendResponse(exchange, 200, json.toString());
                    
                } else if (method.equals("POST")) {
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Map<String, String> params = parseJson(body);
                    
                    boolean success = adminDAO.addUser(
                        params.get("username"),
                        params.get("password"),
                        params.get("fullName"),
                        params.get("email"),
                        params.get("phone"),
                        params.get("role"),
                        params.get("department"),
                        params.get("centerName"),
                        params.get("agencyName"),
                        params.get("licenseNumber")
                    );
                    
                    if (success) {
                        sendResponse(exchange, 201, "{\"success\": true, \"message\": \"User added successfully\"}");
                    } else {
                        sendResponse(exchange, 500, "{\"success\": false, \"message\": \"Failed to add user\"}");
                    }
                    
                } else if (method.equals("PUT")) {
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Map<String, String> params = parseJson(body);
                    
                    boolean success = adminDAO.updateUser(
                        Long.parseLong(params.get("id")),
                        params.get("username"),
                        params.get("fullName"),
                        params.get("email"),
                        params.get("phone"),
                        params.get("role"),
                        params.get("department"),
                        params.get("centerName"),
                        params.get("agencyName"),
                        params.get("licenseNumber"),
                        Boolean.parseBoolean(params.get("isActive"))
                    );
                    
                    if (success) {
                        sendResponse(exchange, 200, "{\"success\": true, \"message\": \"User updated successfully\"}");
                    } else {
                        sendResponse(exchange, 500, "{\"success\": false, \"message\": \"Failed to update user\"}");
                    }
                    
                } else if (method.equals("DELETE")) {
                    String query = exchange.getRequestURI().getQuery();
                    if (query == null) {
                        sendResponse(exchange, 400, "{\"success\": false, \"message\": \"User ID required\"}");
                        return;
                    }
                    
                    Map<String, String> params = parseQuery(query);
                    Long id = Long.parseLong(params.get("id"));
                    
                    boolean success = adminDAO.deleteUser(id);
                    
                    if (success) {
                        sendResponse(exchange, 200, "{\"success\": true, \"message\": \"User deleted successfully\"}");
                    } else {
                        sendResponse(exchange, 500, "{\"success\": false, \"message\": \"Failed to delete user\"}");
                    }
                } else {
                    sendResponse(exchange, 405, "{\"success\": false, \"message\": \"Method not allowed\"}");
                }
                
            } catch (Exception e) {
                System.err.println("Admin users error: " + e.getMessage());
                e.printStackTrace();
                sendResponse(exchange, 500, "{\"success\": false, \"message\": \"Server error\"}");
            }
        }
    }

    static class AdminVehiclesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
            
            if (exchange.getRequestMethod().equals("OPTIONS")) {
                exchange.sendResponseHeaders(200, -1);
                return;
            }
            
            AdminDAO adminDAO = new AdminDAO();
            
            try {
                String method = exchange.getRequestMethod();
                
                if (method.equals("GET")) {
                    java.util.List<java.util.Map<String, Object>> vehicles = adminDAO.getAllVehicles();
                    
                    StringBuilder json = new StringBuilder();
                    json.append("{\"success\": true, \"message\": \"Vehicles retrieved\", \"data\": [");
                    
                    for (int i = 0; i < vehicles.size(); i++) {
                        if (i > 0) json.append(",");
                        java.util.Map<String, Object> vehicle = vehicles.get(i);
                        json.append("{");
                        json.append("\"vehicleNo\":\"").append(vehicle.get("vehicleNo")).append("\",");
                        json.append("\"ownerId\":").append(vehicle.get("ownerId")).append(",");
                        json.append("\"vehicleType\":\"").append(vehicle.get("vehicleType")).append("\",");
                        json.append("\"make\":\"").append(vehicle.get("make") != null ? vehicle.get("make") : "").append("\",");
                        json.append("\"model\":\"").append(vehicle.get("model") != null ? vehicle.get("model") : "").append("\",");
                        json.append("\"fuelType\":\"").append(vehicle.get("fuelType") != null ? vehicle.get("fuelType") : "").append("\",");
                        json.append("\"registrationDate\":\"").append(vehicle.get("registrationDate") != null ? vehicle.get("registrationDate") : "").append("\",");
                        json.append("\"registrationState\":\"").append(vehicle.get("registrationState") != null ? vehicle.get("registrationState") : "").append("\",");
                        json.append("\"insuranceStatus\":\"").append(vehicle.get("insuranceStatus")).append("\",");
                        json.append("\"pucStatus\":\"").append(vehicle.get("pucStatus")).append("\",");
                        json.append("\"rcStatus\":\"").append(vehicle.get("rcStatus")).append("\",");
                        json.append("\"engineNumber\":\"").append(vehicle.get("engineNumber") != null ? vehicle.get("engineNumber") : "").append("\",");
                        json.append("\"chassisNumber\":\"").append(vehicle.get("chassisNumber") != null ? vehicle.get("chassisNumber") : "").append("\",");
                        json.append("\"blacklisted\":").append(vehicle.get("blacklisted"));
                        json.append("}");
                    }
                    
                    json.append("]}");
                    sendResponse(exchange, 200, json.toString());
                    
                } else if (method.equals("POST")) {
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Map<String, String> params = parseJson(body);
                    
                    boolean success = adminDAO.addVehicle(
                        params.get("vehicleNo"),
                        Long.parseLong(params.get("ownerId")),
                        params.get("vehicleType"),
                        params.get("make"),
                        params.get("model"),
                        params.get("fuelType"),
                        params.get("registrationDate"),
                        params.get("registrationState"),
                        params.get("insuranceStatus"),
                        params.get("pucStatus"),
                        params.get("rcStatus"),
                        params.get("engineNumber"),
                        params.get("chassisNumber"),
                        Boolean.parseBoolean(params.get("blacklisted"))
                    );
                    
                    if (success) {
                        sendResponse(exchange, 201, "{\"success\": true, \"message\": \"Vehicle added successfully\"}");
                    } else {
                        sendResponse(exchange, 500, "{\"success\": false, \"message\": \"Failed to add vehicle\"}");
                    }
                    
                } else if (method.equals("DELETE")) {
                    String query = exchange.getRequestURI().getQuery();
                    if (query == null) {
                        sendResponse(exchange, 400, "{\"success\": false, \"message\": \"Vehicle number required\"}");
                        return;
                    }
                    
                    Map<String, String> params = parseQuery(query);
                    String vehicleNo = params.get("vehicleNo");
                    
                    boolean success = adminDAO.deleteVehicle(vehicleNo);
                    
                    if (success) {
                        sendResponse(exchange, 200, "{\"success\": true, \"message\": \"Vehicle deleted successfully\"}");
                    } else {
                        sendResponse(exchange, 500, "{\"success\": false, \"message\": \"Failed to delete vehicle\"}");
                    }
                } else {
                    sendResponse(exchange, 405, "{\"success\": false, \"message\": \"Method not allowed\"}");
                }
                
            } catch (Exception e) {
                System.err.println("Admin vehicles error: " + e.getMessage());
                e.printStackTrace();
                sendResponse(exchange, 500, "{\"success\": false, \"message\": \"Server error\"}");
            }
        }
    }

    static class AdminChallansHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
            
            if (exchange.getRequestMethod().equals("OPTIONS")) {
                exchange.sendResponseHeaders(200, -1);
                return;
            }
            
            AdminDAO adminDAO = new AdminDAO();
            
            try {
                String method = exchange.getRequestMethod();
                
                if (method.equals("GET")) {
                    java.util.List<java.util.Map<String, Object>> challans = adminDAO.getAllChallans();
                    
                    StringBuilder json = new StringBuilder();
                    json.append("{\"success\": true, \"message\": \"Challans retrieved\", \"data\": [");
                    
                    for (int i = 0; i < challans.size(); i++) {
                        if (i > 0) json.append(",");
                        java.util.Map<String, Object> challan = challans.get(i);
                        json.append("{");
                        json.append("\"challanId\":").append(challan.get("challanId")).append(",");
                        json.append("\"challanNumber\":\"").append(challan.get("challanNumber")).append("\",");
                        json.append("\"vehicleNo\":\"").append(challan.get("vehicleNo")).append("\",");
                        json.append("\"officerId\":\"").append(challan.get("officerId")).append("\",");
                        json.append("\"violationIds\":\"").append(challan.get("violationIds")).append("\",");
                        json.append("\"totalFineAmount\":").append(challan.get("totalFineAmount")).append(",");
                        json.append("\"challanDate\":\"").append(challan.get("challanDate")).append("\",");
                        json.append("\"location\":\"").append(challan.get("location")).append("\",");
                        json.append("\"status\":\"").append(challan.get("status")).append("\",");
                        json.append("\"paymentStatus\":\"").append(challan.get("paymentStatus")).append("\"");
                        json.append("}");
                    }
                    
                    json.append("]}");
                    sendResponse(exchange, 200, json.toString());
                    
                } else if (method.equals("POST")) {
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Map<String, String> params = parseJson(body);
                    
                    boolean success = adminDAO.addChallan(
                        params.get("challanNumber"),
                        params.get("vehicleNo"),
                        params.get("officerId"),
                        params.get("violationIds"),
                        Integer.parseInt(params.get("totalFineAmount")),
                        params.get("challanDate"),
                        params.get("location"),
                        params.get("status"),
                        params.get("remarks")
                    );
                    
                    if (success) {
                        sendResponse(exchange, 201, "{\"success\": true, \"message\": \"Challan added successfully\"}");
                    } else {
                        sendResponse(exchange, 500, "{\"success\": false, \"message\": \"Failed to add challan\"}");
                    }
                    
                } else if (method.equals("DELETE")) {
                    String query = exchange.getRequestURI().getQuery();
                    if (query == null) {
                        sendResponse(exchange, 400, "{\"success\": false, \"message\": \"Challan ID required\"}");
                        return;
                    }
                    
                    Map<String, String> params = parseQuery(query);
                    Long challanId = Long.parseLong(params.get("id"));
                    
                    boolean success = adminDAO.deleteChallan(challanId);
                    
                    if (success) {
                        sendResponse(exchange, 200, "{\"success\": true, \"message\": \"Challan deleted successfully\"}");
                    } else {
                        sendResponse(exchange, 500, "{\"success\": false, \"message\": \"Failed to delete challan\"}");
                    }
                } else {
                    sendResponse(exchange, 405, "{\"success\": false, \"message\": \"Method not allowed\"}");
                }
                
            } catch (Exception e) {
                System.err.println("Admin challans error: " + e.getMessage());
                e.printStackTrace();
                sendResponse(exchange, 500, "{\"success\": false, \"message\": \"Server error\"}");
            }
        }
    }

    static class AdminPUCHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
            
            if (exchange.getRequestMethod().equals("OPTIONS")) {
                exchange.sendResponseHeaders(200, -1);
                return;
            }
            
            AdminDAO adminDAO = new AdminDAO();
            
            try {
                String method = exchange.getRequestMethod();
                
                if (method.equals("GET")) {
                    java.util.List<java.util.Map<String, Object>> certificates = adminDAO.getAllPUCCertificates();
                    
                    StringBuilder json = new StringBuilder();
                    json.append("{\"success\": true, \"message\": \"PUC certificates retrieved\", \"data\": [");
                    
                    for (int i = 0; i < certificates.size(); i++) {
                        if (i > 0) json.append(",");
                        java.util.Map<String, Object> cert = certificates.get(i);
                        json.append("{");
                        json.append("\"pucId\":").append(cert.get("pucId")).append(",");
                        json.append("\"certificateNumber\":\"").append(cert.get("certificateNumber")).append("\",");
                        json.append("\"vehicleNo\":\"").append(cert.get("vehicleNo")).append("\",");
                        json.append("\"ownerName\":\"").append(cert.get("ownerName") != null ? cert.get("ownerName") : "").append("\",");
                        json.append("\"vehicleType\":\"").append(cert.get("vehicleType") != null ? cert.get("vehicleType") : "").append("\",");
                        json.append("\"fuelType\":\"").append(cert.get("fuelType") != null ? cert.get("fuelType") : "").append("\",");
                        json.append("\"testDateTime\":\"").append(cert.get("testDateTime")).append("\",");
                        json.append("\"validFrom\":\"").append(cert.get("validFrom")).append("\",");
                        json.append("\"validUpto\":\"").append(cert.get("validUpto")).append("\",");
                        json.append("\"status\":\"").append(cert.get("status")).append("\",");
                        json.append("\"centerName\":\"").append(cert.get("centerName") != null ? cert.get("centerName") : "").append("\"");
                        json.append("}");
                    }
                    
                    json.append("]}");
                    sendResponse(exchange, 200, json.toString());
                    
                } else if (method.equals("POST")) {
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Map<String, String> params = parseJson(body);
                    
                    boolean success = adminDAO.addPUCCertificate(
                        params.get("certificateNumber"),
                        params.get("vehicleNo"),
                        params.get("ownerName"),
                        params.get("vehicleType"),
                        params.get("fuelType"),
                        params.get("testDateTime"),
                        params.get("validFrom"),
                        params.get("validUpto"),
                        params.get("status"),
                        params.get("centerName")
                    );
                    
                    if (success) {
                        sendResponse(exchange, 201, "{\"success\": true, \"message\": \"PUC certificate added successfully\"}");
                    } else {
                        sendResponse(exchange, 500, "{\"success\": false, \"message\": \"Failed to add PUC certificate\"}");
                    }
                    
                } else if (method.equals("DELETE")) {
                    String query = exchange.getRequestURI().getQuery();
                    if (query == null) {
                        sendResponse(exchange, 400, "{\"success\": false, \"message\": \"PUC ID required\"}");
                        return;
                    }
                    
                    Map<String, String> params = parseQuery(query);
                    Long pucId = Long.parseLong(params.get("id"));
                    
                    boolean success = adminDAO.deletePUCCertificate(pucId);
                    
                    if (success) {
                        sendResponse(exchange, 200, "{\"success\": true, \"message\": \"PUC certificate deleted successfully\"}");
                    } else {
                        sendResponse(exchange, 500, "{\"success\": false, \"message\": \"Failed to delete PUC certificate\"}");
                    }
                } else {
                    sendResponse(exchange, 405, "{\"success\": false, \"message\": \"Method not allowed\"}");
                }
                
            } catch (Exception e) {
                System.err.println("Admin PUC error: " + e.getMessage());
                e.printStackTrace();
                sendResponse(exchange, 500, "{\"success\": false, \"message\": \"Server error\"}");
            }
        }
    }

    static class AdminOwnersHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
            
            if (exchange.getRequestMethod().equals("OPTIONS")) {
                exchange.sendResponseHeaders(200, -1);
                return;
            }
            
            AdminDAO adminDAO = new AdminDAO();
            
            try {
                String method = exchange.getRequestMethod();
                
                if (method.equals("GET")) {
                    java.util.List<java.util.Map<String, Object>> owners = adminDAO.getAllOwners();
                    
                    StringBuilder json = new StringBuilder();
                    json.append("{\"success\": true, \"message\": \"Owners retrieved\", \"data\": [");
                    
                    for (int i = 0; i < owners.size(); i++) {
                        if (i > 0) json.append(",");
                        java.util.Map<String, Object> owner = owners.get(i);
                        json.append("{");
                        json.append("\"ownerId\":").append(owner.get("ownerId")).append(",");
                        json.append("\"ownerName\":\"").append(owner.get("ownerName")).append("\",");
                        json.append("\"mobile\":\"").append(owner.get("mobile") != null ? owner.get("mobile") : "").append("\",");
                        json.append("\"email\":\"").append(owner.get("email") != null ? owner.get("email") : "").append("\",");
                        json.append("\"address\":\"").append(owner.get("address") != null ? owner.get("address") : "").append("\"");
                        json.append("}");
                    }
                    
                    json.append("]}");
                    sendResponse(exchange, 200, json.toString());
                    
                } else if (method.equals("POST")) {
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Map<String, String> params = parseJson(body);
                    
                    boolean success = adminDAO.addOwner(
                        params.get("ownerName"),
                        params.get("mobile"),
                        params.get("email"),
                        params.get("address")
                    );
                    
                    if (success) {
                        sendResponse(exchange, 201, "{\"success\": true, \"message\": \"Owner added successfully\"}");
                    } else {
                        sendResponse(exchange, 500, "{\"success\": false, \"message\": \"Failed to add owner\"}");
                    }
                    
                } else if (method.equals("DELETE")) {
                    String query = exchange.getRequestURI().getQuery();
                    if (query == null) {
                        sendResponse(exchange, 400, "{\"success\": false, \"message\": \"Owner ID required\"}");
                        return;
                    }
                    
                    Map<String, String> params = parseQuery(query);
                    Long ownerId = Long.parseLong(params.get("id"));
                    
                    boolean success = adminDAO.deleteOwner(ownerId);
                    
                    if (success) {
                        sendResponse(exchange, 200, "{\"success\": true, \"message\": \"Owner deleted successfully\"}");
                    } else {
                        sendResponse(exchange, 500, "{\"success\": false, \"message\": \"Failed to delete owner\"}");
                    }
                } else {
                    sendResponse(exchange, 405, "{\"success\": false, \"message\": \"Method not allowed\"}");
                }
                
            } catch (Exception e) {
                System.err.println("Admin owners error: " + e.getMessage());
                e.printStackTrace();
                sendResponse(exchange, 500, "{\"success\": false, \"message\": \"Server error\"}");
            }
        }
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }

    private static Map<String, String> parseJson(String json) {
        Map<String, String> map = new HashMap<>();
        json = json.trim();
        if (json.startsWith("{") && json.endsWith("}")) {
            json = json.substring(1, json.length() - 1);
            String[] pairs = json.split(",");
            for (String pair : pairs) {
                String[] kv = pair.split(":");
                if (kv.length == 2) {
                    String key = kv[0].trim().replaceAll("\"", "");
                    String value = kv[1].trim().replaceAll("\"", "");
                    map.put(key, value);
                }
            }
        }
        return map;
    }

    private static Map<String, String> parseQuery(String query) {
        Map<String, String> map = new HashMap<>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=");
            if (kv.length == 2) {
                map.put(kv[0], kv[1]);
            }
        }
        return map;
    }
}
