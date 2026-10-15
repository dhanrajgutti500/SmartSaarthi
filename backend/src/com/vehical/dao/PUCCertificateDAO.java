package com.vehical.dao;

import com.vehical.DatabaseConfig;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class PUCCertificateDAO {

    public static boolean insertPUCCertificate(
            String certificateNumber,
            String vehicleNo,
            String ownerName,
            String vehicleType,
            String fuelType,
            String registrationDate,
            String engineNumber,
            String chassisNumber,
            String centerId,
            String centerName,
            String testDateTime,
            String emissionNorms,
            Double odometerKm,
            Double coPercent,
            Double hcPpm,
            Double co2Percent,
            Double o2Percent,
            Double lambdaValue,
            Double smokeDensity,
            Double rpm,
            Double temperature,
            String pollutionUnderControl,
            String passStatus,
            String validFrom,
            String validUpto,
            Long issuedByUserId,
            String issuedByName
    ) {
        String sql = "INSERT INTO puc_certificate " +
                "(certificate_number, vehicle_no, owner_name, vehicle_type, fuel_type, " +
                "registration_date, engine_number, chassis_number, center_id, center_name, " +
                "test_date_time, emission_norms, odometer_km, co_percent, hc_ppm, " +
                "co2_percent, lambda_value, pollution_under_control, valid_from, valid_upto, " +
                "status, issued_by_user_id, issued_by_name) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'Valid', ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, certificateNumber);
            ps.setString(2, vehicleNo);
            ps.setString(3, ownerName);
            ps.setString(4, vehicleType);
            ps.setString(5, fuelType);
            ps.setString(6, registrationDate);
            ps.setString(7, engineNumber);
            ps.setString(8, chassisNumber);
            ps.setString(9, centerId);
            ps.setString(10, centerName);
            ps.setString(11, testDateTime);
            ps.setString(12, emissionNorms);
            ps.setObject(13, odometerKm);
            ps.setObject(14, coPercent);
            ps.setObject(15, hcPpm);
            ps.setObject(16, co2Percent);
            ps.setObject(17, lambdaValue);
            ps.setString(18, pollutionUnderControl);
            ps.setString(19, validFrom);
            ps.setString(20, validUpto);
            ps.setObject(21, issuedByUserId);
            ps.setString(22, issuedByName);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error inserting PUC certificate: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateVehiclePUCStatus(String vehicleNo, String newStatus) {
        String sql = "UPDATE vehicle SET puc_status = ?, updated_at = SYSTIMESTAMP WHERE vehicle_no = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setString(2, vehicleNo);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating vehicle PUC status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static List<Map<String, Object>> getAllPUCCertificates() {
        List<Map<String, Object>> certificates = new ArrayList<>();
        String sql = "SELECT puc_id, certificate_number, vehicle_no, owner_name, vehicle_type, " +
                "fuel_type, center_name, test_date_time, valid_from, valid_upto, status, " +
                "co_percent, hc_ppm, co2_percent, lambda_value, smoke_density, " +
                "odometer_km, issued_by_name, created_at " +
                "FROM puc_certificate ORDER BY test_date_time DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> cert = new HashMap<>();
                cert.put("pucId", rs.getLong("puc_id"));
                cert.put("certificateNumber", rs.getString("certificate_number"));
                cert.put("vehicleNo", rs.getString("vehicle_no"));
                cert.put("ownerName", rs.getString("owner_name"));
                cert.put("vehicleType", rs.getString("vehicle_type"));
                cert.put("fuelType", rs.getString("fuel_type"));
                cert.put("centerName", rs.getString("center_name"));
                cert.put("testDateTime", rs.getTimestamp("test_date_time").toString());
                cert.put("validFrom", rs.getDate("valid_from").toString());
                cert.put("validUpto", rs.getDate("valid_upto").toString());
                cert.put("status", rs.getString("status"));
                cert.put("coPercent", rs.getObject("co_percent"));
                cert.put("hcPpm", rs.getObject("hc_ppm"));
                cert.put("co2Percent", rs.getObject("co2_percent"));
                cert.put("lambdaValue", rs.getObject("lambda_value"));
                cert.put("smokeDensity", rs.getObject("smoke_density"));
                cert.put("odometerKm", rs.getObject("odometer_km"));
                cert.put("issuedByName", rs.getString("issued_by_name"));
                cert.put("createdAt", rs.getTimestamp("created_at").toString());
                certificates.add(cert);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching PUC certificates: " + e.getMessage());
            e.printStackTrace();
        }

        return certificates;
    }
}
