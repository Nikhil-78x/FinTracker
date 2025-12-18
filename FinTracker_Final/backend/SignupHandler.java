
import com.sun.net.httpserver.*;
import java.io.*;
import java.sql.*;
import org.json.JSONObject;

public class SignupHandler implements HttpHandler {
    public void handle(HttpExchange ex) throws IOException {
        try {
            ex.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            String body = new String(ex.getRequestBody().readAllBytes());
            JSONObject j = new JSONObject(body);

            Connection c = DB.getConnection();
            PreparedStatement ps = c.prepareStatement(
                "INSERT INTO users(name,email,password) VALUES(?,?,?)");
            ps.setString(1, j.getString("name"));
            ps.setString(2, j.getString("email"));
            ps.setString(3, PasswordUtil.hash(j.getString("password")));
            ps.executeUpdate();

            byte[] res = "{ "status": "ok" }".getBytes();
            ex.sendResponseHeaders(200, res.length);
            ex.getResponseBody().write(res);
            ex.close();
        } catch(Exception e){ e.printStackTrace(); }
    }
}
