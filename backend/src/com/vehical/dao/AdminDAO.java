package com.vehical.dao;

import com.vehical.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminDAO {

    // ==================== USERS CRUD ====================
    
    public List<Map<String, Object>> getAllUsers() {
        List<Map<String, Object>> users = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            String sql = "SELECT id, username, full_name, email, phone, role, department, center_name, agency_name, license_number, is_active FROM users ORDER BY id";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> user = new HashMap<>();
                user.put("id", rs.getLong("id"));
                user.put("username", rs.getString("username"));
                user.put("fullName", rs.getString("full_name"));
                user.put("email", rs.getString("email"));
                user.put("phone", rs.getString("phone"));
                user.put("role", rs.getString("role"));
                user.put("department", rs.getString("department"));
                user.put("centerName", rs.getString("center_name"));
                user.put("agencyName", rs.getString("agency_name"));
                user.put("licenseNumber", rs.getString("license_number"));
                user.put("isActive", rs.getInt("is_active") == 1);
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Error getting users: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return users;
    }

    public boolean addUser(String username, String password, String fullName, String email, String phone, String role, String department, String centerName, String agencyName, String licenseNumber) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            String sql = "INSERT INTO users (username, password, full_name, email, phone, role, department, center_name, agency_name, license_number, is_active) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, fullName);
            pstmt.setString(4, email);
            pstmt.setString(5, phone);
            pstmt.setString(6, role);
            pstmt.setString(7, department);
            pstmt.setString(8, centerName);
            pstmt.setString(9, agencyName);
            pstmt.setString(10, licenseNumber);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean updateUser(Long id, String username, String fullName, String email, String phone, String role, String department, String centerName, String agencyName, String licenseNumber, Boolean isActive) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            String sql = "UPDATE users SET username = ?, full_name = ?, email = ?, phone = ?, role = ?, department = ?, " +
                         "center_name = ?, agency_name = ?, license_number = ?, is_active = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, fullName);
            pstmt.setString(3, email);
            pstmt.setString(4, phone);
            pstmt.setString(5, role);
            pstmt.setString(6, department);
            pstmt.setString(7, centerName);
            pstmt.setString(8, agencyName);
            pstmt.setString(9, licenseNumber);
            pstmt.setInt(10, isActive ? 1 : 0);
            pstmt.setLong(11, id);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean deleteUser(Long id) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            String sql = "DELETE FROM users WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, id);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // ==================== VEHICLES CRUD ====================
    
    public List<Map<String, Object>> getAllVehicles() {
        List<Map<String, Object>> vehicles = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            String sql = "SELECT vehicle_no, owner_id, vehicle_type, make, model, fuel_type, registration_date, " +
                         "registration_state, insurance_status, puc_status, rc_status, engine_number, chassis_number, blacklisted " +
                         "FROM vehicle ORDER BY vehicle_no";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> vehicle = new HashMap<>();
                vehicle.put("vehicleNo", rs.getString("vehicle_no"));
                vehicle.put("ownerId", rs.getLong("owner_id"));
                vehicle.put("vehicleType", rs.getString("vehicle_type"));
                vehicle.put("make", rs.getString("make"));
                vehicle.put("model", rs.getString("model"));
                vehicle.put("fuelType", rs.getString("fuel_type"));
                vehicle.put("registrationDate", rs.getString("registration_date"));
                vehicle.put("registrationState", rs.getString("registration_state"));
                vehicle.put("insuranceStatus", rs.getString("insurance_status"));
                vehicle.put("pucStatus", rs.getString("puc_status"));
                vehicle.put("rcStatus", rs.getString("rc_status"));
                vehicle.put("engineNumber", rs.getString("engine_number"));
                vehicle.put("chassisNumber", rs.getString("chassis_number"));
                vehicle.put("blacklisted", rs.getInt("blacklisted") == 1);
                vehicles.add(vehicle);
            }
        } catch (SQLException e) {
            System.err.println("Error getting vehicles: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return vehicles;
    }

    public boolean addVehicle(String vehicleNo, Long ownerId, String vehicleType, String make, String model, String fuelType, 
                              String registrationDate, String registrationState, String insuranceStatus, String pucStatus, 
                              String rcStatus, String engineNumber, String chassisNumber, Boolean blacklisted) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            String sql = "INSERT INTO vehicle (vehicle_no, owner_id, vehicle_type, make, model, fuel_type, registration_date, " +
                         "registration_state, insurance_status, puc_status, rc_status, engine_number, chassis_number, blacklisted) " +
                         "VALUES (?, ?, ?, ?, ?, ?, TO_DATE(?, 'YYYY-MM-DD'), ?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, vehicleNo);
            pstmt.setLong(2, ownerId);
            pstmt.setString(3, vehicleType);
            pstmt.setString(4, make);
            pstmt.setString(5, model);
            pstmt.setString(6, fuelType);
            pstmt.setString(7, registrationDate);
            pstmt.setString(8, registrationState);
            pstmt.setString(9, insuranceStatus);
            pstmt.setString(10, pucStatus);
            pstmt.setString(11, rcStatus);
            pstmt.setString(12, engineNumber);
            pstmt.setString(13, chassisNumber);
            pstmt.setInt(14, blacklisted ? 1 : 0);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding vehicle: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean deleteVehicle(String vehicleNo) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            String sql = "DELETE FROM vehicle WHERE vehicle_no = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, vehicleNo);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting vehicle: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // ==================== CHALLANS CRUD ====================
    
    public List<Map<String, Object>> getAllChallans() {
        List<Map<String, Object>> challans = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            String sql = "SELECT challan_id, challan_number, vehicle_no, officer_id, violation_ids, total_fine_amount, " +
                         "TO_CHAR(challan_date, 'YYYY-MM-DD') as challan_date, location, status, payment_status " +
                         "FROM challan ORDER BY challan_date DESC";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> challan = new HashMap<>();
                challan.put("challanId", rs.getLong("challan_id"));
                challan.put("challanNumber", rs.getString("challan_number"));
                challan.put("vehicleNo", rs.getString("vehicle_no"));
                challan.put("officerId", rs.getString("officer_id"));
                challan.put("violationIds", rs.getString("violation_ids"));
                challan.put("totalFineAmount", rs.getInt("total_fine_amount"));
                challan.put("challanDate", rs.getString("challan_date"));
                challan.put("location", rs.getString("location"));
                challan.put("status", rs.getString("status"));
                challan.put("paymentStatus", rs.getString("payment_status"));
                challans.add(challan);
            }
        } catch (SQLException e) {
            System.err.println("Error getting challans: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return challans;
    }

    public boolean addChallan(String challanNumber, String vehicleNo, String officerId, String violationIds, 
                              Integer totalFineAmount, String challanDate, String location, String status, String remarks) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            String sql = "INSERT INTO challan (challan_number, vehicle_no, officer_id, violation_ids, total_fine_amount, " +
                         "challan_date, challan_time, location, status, payment_status, remarks) " +
                         "VALUES (?, ?, ?, ?, ?, TO_DATE(?, 'YYYY-MM-DD'), '00:00:00', ?, ?, 'Unpaid', ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, challanNumber);
            pstmt.setString(2, vehicleNo);
            pstmt.setString(3, officerId);
            pstmt.setString(4, violationIds);
            pstmt.setInt(5, totalFineAmount);
            pstmt.setString(6, challanDate);
            pstmt.setString(7, location);
            pstmt.setString(8, status);
            pstmt.setString(9, remarks);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding challan: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean deleteChallan(Long challanId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            String sql = "DELETE FROM challan WHERE challan_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, challanId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting challan: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // ==================== PUC CERTIFICATES CRUD ====================
    
    public List<Map<String, Object>> getAllPUCCertificates() {
        List<Map<String, Object>> certificates = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            String sql = "SELECT puc_id, certificate_number, vehicle_no, owner_name, vehicle_type, fuel_type, " +
                         "TO_CHAR(test_date_time, 'YYYY-MM-DD HH24:MI:SS') as test_date_time, " +
                         "TO_CHAR(valid_from, 'YYYY-MM-DD') as valid_from, " +
                         "TO_CHAR(valid_upto, 'YYYY-MM-DD') as valid_upto, status, center_name " +
                         "FROM puc_certificate ORDER BY test_date_time DESC";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> cert = new HashMap<>();
                cert.put("pucId", rs.getLong("puc_id"));
                cert.put("certificateNumber", rs.getString("certificate_number"));
                cert.put("vehicleNo", rs.getString("vehicle_no"));
                cert.put("ownerName", rs.getString("owner_name"));
                cert.put("vehicleType", rs.getString("vehicle_type"));
                cert.put("fuelType", rs.getString("fuel_type"));
                cert.put("testDateTime", rs.getString("test_date_time"));
                cert.put("validFrom", rs.getString("valid_from"));
                cert.put("validUpto", rs.getString("valid_upto"));
                cert.put("status", rs.getString("status"));
                cert.put("centerName", rs.getString("center_name"));
                certificates.add(cert);
            }
        } catch (SQLException e) {
            System.err.println("Error getting PUC certificates: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return certificates;
    }

    public boolean addPUCCertificate(String certificateNumber, String vehicleNo, String ownerName, String vehicleType, 
                                     String fuelType, String testDateTime, String validFrom, String validUpto, 
                                     String status, String centerName) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            String sql = "INSERT INTO puc_certificate (certificate_number, vehicle_no, owner_name, vehicle_type, fuel_type, " +
                         "test_date_time, valid_from, valid_upto, status, center_name) " +
                         "VALUES (?, ?, ?, ?, ?, TO_TIMESTAMP(?, 'YYYY-MM-DD HH24:MI:SS'), TO_DATE(?, 'YYYY-MM-DD'), " +
                         "TO_DATE(?, 'YYYY-MM-DD'), ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, certificateNumber);
            pstmt.setString(2, vehicleNo);
            pstmt.setString(3, ownerName);
            pstmt.setString(4, vehicleType);
            pstmt.setString(5, fuelType);
            pstmt.setString(6, testDateTime);
            pstmt.setString(7, validFrom);
            pstmt.setString(8, validUpto);
            pstmt.setString(9, status);
            pstmt.setString(10, centerName);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding PUC certificate: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean deletePUCCertificate(Long pucId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            String sql = "DELETE FROM puc_certificate WHERE puc_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, pucId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting PUC certificate: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // ==================== OWNERS CRUD ====================
    
    public List<Map<String, Object>> getAllOwners() {
        List<Map<String, Object>> owners = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            String sql = "SELECT owner_id, owner_name, mobile, email, address FROM owner ORDER BY owner_id";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> owner = new HashMap<>();
                owner.put("ownerId", rs.getLong("owner_id"));
                owner.put("ownerName", rs.getString("owner_name"));
                owner.put("mobile", rs.getString("mobile"));
                owner.put("email", rs.getString("email"));
                owner.put("address", rs.getString("address"));
                owners.add(owner);
            }
        } catch (SQLException e) {
            System.err.println("Error getting owners: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return owners;
    }

    public boolean addOwner(String ownerName, String mobile, String email, String address) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            String sql = "INSERT INTO owner (owner_name, mobile, email, address) VALUES (?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, ownerName);
            pstmt.setString(2, mobile);
            pstmt.setString(3, email);
            pstmt.setString(4, address);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding owner: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean deleteOwner(Long ownerId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            String sql = "DELETE FROM owner WHERE owner_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, ownerId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting owner: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
