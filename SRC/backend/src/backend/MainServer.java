package src.backend;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

public class MainServer {
    public static void main(String[] args) throws Exception {
        int port = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        System.out.println("Starting server on port " + port);

        server.createContext("/signup", new SignupHandler());
        server.createContext("/login", new LoginHandler());
        server.createContext("/addTransaction", new AddTransactionHandler());
        server.createContext("/getTransactions", new GetTransactionsHandler());

        server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
        server.start();
    }

    static Map<String, String> parseForm(InputStream is, String rawQuery) throws IOException {
        String body = "";
        if (is != null) {
            body = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("&"));
        }
        if ((body == null || body.isEmpty()) && rawQuery != null) {
            body = rawQuery;
        }
        Map<String, String> map = new HashMap<>();
        if (body == null || body.isEmpty()) return map;
        String[] pairs = body.split("&");
        for (String p : pairs) {
            String[] kv = p.split("=", 2);
            if (kv.length == 2) {
                map.put(Utils.urlDecode(kv[0]), Utils.urlDecode(kv[1]));
            }
        }
        return map;
    }

    static class SignupHandler implements HttpHandler {
        public void handle(HttpExchange ex) throws IOException {
            try {
                Map<String, String> params = parseForm(ex.getRequestBody(), null);
                String username = params.get("username");
                String email = params.get("email");
                String password = params.get("password");
                if (username == null || email == null || password == null) {
                    sendResponse(ex, 400, "Missing fields (username,email,password required)");
                    return;
                }
                String hash = Utils.sha256(password);
                UserDAO dao = new UserDAO();
                boolean ok = dao.createUser(username, email, hash);
                if (ok) sendResponse(ex, 200, "signup_ok");
                else sendResponse(ex, 500, "signup_failed");
            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(ex, 500, "error: " + e.getMessage());
            }
        }
    }

    static class LoginHandler implements HttpHandler {
        public void handle(HttpExchange ex) throws IOException {
            try {
                Map<String, String> params = parseForm(ex.getRequestBody(), null);
                String email = params.get("email");
                String password = params.get("password");
                if (email == null || password == null) {
                    sendResponse(ex, 400, "Missing fields (email,password required)");
                    return;
                }
                String hash = Utils.sha256(password);
                UserDAO dao = new UserDAO();
                boolean ok = dao.checkCredentials(email, hash);
                if (ok) {
                    // simple token: not secure in production
                    String token = Base64.getEncoder().encodeToString((email + ":" + System.currentTimeMillis()).getBytes(StandardCharsets.UTF_8));
                    sendResponse(ex, 200, "{\"status\":\"ok\",\"token\":\""+token+"\"}");
                } else sendResponse(ex, 401, "invalid_credentials");
            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(ex, 500, "error: " + e.getMessage());
            }
        }
    }

    static class AddTransactionHandler implements HttpHandler {
        public void handle(HttpExchange ex) throws IOException {
            try {
                Map<String, String> params = parseForm(ex.getRequestBody(), ex.getRequestURI().getQuery());
                String email = params.get("email");
                String amountS = params.get("amount");
                String description = params.getOrDefault("description", "");
                String category = params.getOrDefault("category", "Other");
                String dateS = params.get("date");
                String recurringS = params.getOrDefault("recurring", "false");
                if (email==null || amountS==null || dateS==null) {
                    sendResponse(ex, 400, "Missing fields (email, amount, date required)");
                    return;
                }
                double amount = Double.parseDouble(amountS);
                boolean recurring = "true".equalsIgnoreCase(recurringS) || "1".equals(recurringS);
                Date date = Date.valueOf(dateS);
                TransactionDAO dao = new TransactionDAO();
                boolean ok = dao.addTransaction(email, amount, description, category, date, recurring);
                if (ok) sendResponse(ex, 200, "transaction_added");
                else sendResponse(ex, 500, "failed");
            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(ex, 500, "error: " + e.getMessage());
            }
        }
    }

    static class GetTransactionsHandler implements HttpHandler {
        public void handle(HttpExchange ex) throws IOException {
            try {
                String query = ex.getRequestURI().getQuery();
                Map<String,String> params = parseForm(null, query);
                String email = params.get("email");
                if (email == null) {
                    sendResponse(ex, 400, "Missing email param");
                    return;
                }
                TransactionDAO dao = new TransactionDAO();
                java.util.List<String> list = dao.getTransactions(email);
                String resp = "[" + String.join(",", list) + "]";
                sendResponse(ex, 200, resp);
            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(ex, 500, "error: " + e.getMessage());
            }
        }
    }

    static void sendResponse(HttpExchange ex, int code, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        ex.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }
}
