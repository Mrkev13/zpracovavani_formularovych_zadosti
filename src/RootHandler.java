import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

public class RootHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange ex){
        try {
            String resp = "FormProcessingServer is running";
            ex.sendResponseHeaders(200, resp.getBytes().length);
            ex.getResponseBody().write(resp.getBytes());
            ex.getResponseBody().close();
        } catch (Exception e){ e.printStackTrace(); }
    }
}
