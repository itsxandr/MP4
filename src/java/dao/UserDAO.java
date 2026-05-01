   /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private String URL;
    private String USER;
    private String PASS;

    public UserDAO(String URL, String USER, String PASS) {
        this.URL = URL;
        this.USER = USER;
        this.PASS = PASS;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT EMAIL FROM USERS WHERE EMAIL = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }
    // new encryption method
    public String getEncryptedPassword(String email) throws SQLException {
        String sql = "SELECT PASSWORD FROM USERS WHERE EMAIL = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("PASSWORD");
            }
        }
        return null;
    }

    public boolean isPasswordCorrect(String email, String password) throws SQLException {
        String sql = "SELECT PASSWORD FROM USERS WHERE EMAIL = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("PASSWORD").equals(password);
            }
        }
        return false;
    }

    public String getUserRole(String email) throws SQLException {
        String sql = "SELECT USERROLE FROM USERS WHERE EMAIL = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("USERROLE");
            }
        }
        return null;
    }

    public List<User> getAllUsersSorted() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM USERS ORDER BY EMAIL ASC";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(new User(
                        rs.getString("EMAIL"),
                        rs.getString("PASSWORD"),
                        rs.getString("USERROLE")
                ));
            }
        }
        return users;
    }

    public User getUserByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM USERS WHERE EMAIL = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getString("EMAIL"),
                        rs.getString("PASSWORD"),
                        rs.getString("USERROLE")
                );
            }
        }
        return null;
    }

    public void addUser(String email, String pass, String role) throws SQLException {
        String sql = "INSERT INTO USERS (EMAIL, PASSWORD, USERROLE) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, pass);
            ps.setString(3, role);
            ps.executeUpdate();
        }
    }

    public void updateUser(String email, String pass, String role) throws SQLException {
        String sql = "UPDATE USERS SET PASSWORD=?, USERROLE=? WHERE EMAIL=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pass);
            ps.setString(2, role);
            ps.setString(3, email);
            ps.executeUpdate();
        }
    }

    public void deleteUser(String email) throws SQLException {
        String sql = "DELETE FROM USERS WHERE EMAIL=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.executeUpdate();
        }
    }
}
