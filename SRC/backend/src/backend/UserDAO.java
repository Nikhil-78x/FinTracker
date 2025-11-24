package src.backend;

import java.sql.*;

public class UserDAO {
    public boolean createUser(String username, String email, String passwordHash) throws SQLException {
        String sql = "INSERT INTO users (username, email, password_hash) VALUES (?, ?, ?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, passwordHash);
            return ps.executeUpdate() == 1;
        }
    }

    public boolean checkCredentials(String email, String passwordHash) throws SQLException {
        String sql = "SELECT id FROM users WHERE email = ? AND password_hash = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, passwordHash);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
