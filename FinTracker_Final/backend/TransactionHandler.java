
import com.sun.net.httpserver.*;
import java.io.*;
import java.sql.*;
import org.json.JSONObject;

public class TransactionHandler implements HttpHandler {
    public void handle(HttpExchange ex) throws IOException {
        try {
            ex.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            if(ex.getRequestMethod().equals("POST")){
                String body = new String(ex.getRequestBody().readAllBytes());
                JSONObject j = new JSONObject(body);
                Connection c = DB.getConnection();
                PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO transactions(user_id,amount,type,category,description) VALUES(?,?,?,?,?)");
                ps.setInt(1, j.getInt("userId"));
                ps.setDouble(2, j.getDouble("amount"));
                ps.setString(3, j.getString("type"));
                ps.setString(4, j.getString("category"));
                ps.setString(5, j.getString("description"));
                ps.executeUpdate();
                ex.sendResponseHeaders(200,0);
            }
            ex.close();
        } catch(Exception e){ e.printStackTrace(); }
    }
}
