package com.vehical.dao;

import com.vehical.DatabaseConfig;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChallanDAO {

    /**
     * Get all challans for a vehicle by its number.
     * Normalizes the vehicle number (removes spaces/hyphens) and matches against
     * the challan table. Returns violation names joined from violation_master.
     */
    public List<Map<String, Object>> getChallansByVehicleNo(String vehicleNo) {
        List<Map<String, Object>> challans = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        if (vehicleNo == null || vehicleNo.trim().isEmpty()) {
            return challans;
        }

        String normalized = vehicleNo.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();

        try {
            conn = DatabaseConfig.getConnection();
            String sql = "SELECT c.challan_id, c.challan_number, c.vehicle_no, c.officer_id, " +
                         "c.violation_ids, c.total_fine_amount, " +
                         "TO_CHAR(c.challan_date, 'YYYY-MM-DD') as challan_date, " +
                         "c.challan_time, c.location, c.status, c.payment_status, c.remarks, " +
                         "c.payment_date, c.payment_reference, " +
                         "vm.violation_name " +
                         "FROM challan c " +
                         "LEFT JOIN violation_master vm ON TO_CHAR(vm.violation_id) = c.violation_ids " +
                         "WHERE REPLACE(REPLACE(UPPER(c.vehicle_no), '-', ''), ' ', '') = ? " +
                         "ORDER BY c.challan_date DESC";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, normalized);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> challan = new HashMap<>();
                challan.put("challanId", rs.getLong("challan_id"));
                challan.put("challanNumber", rs.getString("challan_number"));
                challan.put("vehicleNo", rs.getString("vehicle_no"));
                challan.put("officerId", rs.getString("officer_id"));
                challan.put("violationIds", rs.getString("violation_ids"));
                challan.put("violationName", rs.getString("violation_name") != null ? rs.getString("violation_name") : "Traffic Violation");
                challan.put("totalFineAmount", rs.getInt("total_fine_amount"));
                challan.put("challanDate", rs.getString("challan_date"));
                challan.put("challanTime", rs.getString("challan_time"));
                challan.put("location", rs.getString("location"));
                challan.put("status", rs.getString("status"));
                challan.put("paymentStatus", rs.getString("payment_status"));
                challan.put("remarks", rs.getString("remarks"));
                challan.put("paymentDate", rs.getString("payment_date"));
                challan.put("paymentReference", rs.getString("payment_reference"));
                challans.add(challan);
            }
        } catch (SQLException e) {
            System.err.println("Error getting challans by vehicle: " + e.getMessage());
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

    public boolean updateChallanStatus(String challanNumber, String status, String paymentReference) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            
            // First try to update existing challan
            String sql = "UPDATE challan SET status = ?, payment_status = 'Paid', " +
                         "payment_date = SYSDATE, payment_reference = ?, updated_at = SYSTIMESTAMP " +
                         "WHERE challan_number = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);
            pstmt.setString(2, paymentReference);
            pstmt.setString(3, challanNumber);
            
            int rowsAffected = pstmt.executeUpdate();
            
            // If no rows affected, challan doesn't exist - insert it as paid (for demo purposes)
            if (rowsAffected == 0) {
                pstmt.close();
                
                // Insert the challan as a paid record
                String insertSql = "INSERT INTO challan (challan_number, vehicle_no, officer_id, violation_ids, " +
                                  "total_fine_amount, challan_date, challan_time, location, status, " +
                                  "payment_status, payment_date, payment_reference) " +
                                  "VALUES (?, 'DEMO-VEHICLE', 'PO00000', '15', 0, SYSDATE, '00:00:00', " +
                                  "'Demo Location', ?, 'Paid', SYSDATE, ?)";
                
                pstmt = conn.prepareStatement(insertSql);
                pstmt.setString(1, challanNumber);
                pstmt.setString(2, status);
                pstmt.setString(3, paymentReference);
                
                rowsAffected = pstmt.executeUpdate();
                System.out.println("Inserted new paid challan: " + challanNumber);
            }
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating challan status: " + e.getMessage());
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

    public boolean isChallanPaid(String challanNumber) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            
            String sql = "SELECT payment_status FROM challan WHERE challan_number = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, challanNumber);
            
            rs = pstmt.executeQuery();
            if (rs.next()) {
                String paymentStatus = rs.getString("payment_status");
                return "Paid".equalsIgnoreCase(paymentStatus);
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("Error checking challan payment status: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
