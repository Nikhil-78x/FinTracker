
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/login", new LoginHandler());
        server.createContext("/api/signup", new SignupHandler());
        server.createContext("/api/transactions", new TransactionHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port 8080");
    }
}
