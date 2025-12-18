
import com.sun.net.httpserver.*;
import java.io.*;
import java.sql.*;
import org.json.JSONObject;

public class LoginHandler implements HttpHandler {
    public void handle(HttpExchange ex) throws IOException {
        try {
            ex.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            String body = new String(ex.getRequestBody().readAllBytes());
            JSONObject j = new JSONObject(body);

            Connection c = DB.getConnection();
            PreparedStatement ps = c.prepareStatement(
                "SELECT id,password FROM users WHERE email=?");
            ps.setString(1, j.getString("email"));
            ResultSet rs = ps.executeQuery();

            String res;
            if(rs.next() && rs.getString("password")
                .equals(PasswordUtil.hash(j.getString("password")))){
                res = "{ "userId": "+rs.getInt("id")+" }";
            } else res = "{ "error": "Invalid" }";

            ex.sendResponseHeaders(200, res.length());
            ex.getResponseBody().write(res.getBytes());
            ex.close();
        } catch(Exception e){ e.printStackTrace(); }
    }
}
