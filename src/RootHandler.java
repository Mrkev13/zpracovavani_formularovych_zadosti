import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;

public class RootHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange ex) throws IOException {
        String resp = "FormProcessingServer is running";
        ex.sendResponseHeaders(200, resp.getBytes().length);
        try (var os = ex.getResponseBody()) {
            os.write(resp.getBytes());
        }
    }
}
