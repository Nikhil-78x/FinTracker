package src.backend;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {
    public boolean addTransaction(String email, double amount, String description, String category, Date date, boolean recurring) throws SQLException {
        String sql = "INSERT INTO transactions (user_email, amount, description, category, date, recurring) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setDouble(2, amount);
            ps.setString(3, description);
            ps.setString(4, category);
            ps.setDate(5, date);
            ps.setBoolean(6, recurring);
            return ps.executeUpdate() == 1;
        }
    }

    public List<String> getTransactions(String email) throws SQLException {
        String sql = "SELECT id, amount, description, category, date, recurring FROM transactions WHERE user_email = ? ORDER BY date DESC";
        List<String> out = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(String.format("{\"id\":%d,\"amount\":%.2f,\"description\":\"%s\",\"category\":\"%s\",\"date\":\"%s\",\"recurring\":%s}",
                            rs.getInt("id"),
                            rs.getDouble("amount"),
                            rs.getString("description"),
                            rs.getString("category"),
                            rs.getDate("date").toString(),
                            rs.getBoolean("recurring")));
                }
            }
        }
        return out;
    }
}
