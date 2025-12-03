import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.util.Map;
import java.util.UUID;

public class SubmitHandler implements HttpHandler {

    private final FormProcessingServer server;

    public SubmitHandler(FormProcessingServer server){
        this.server = server;
    }

    @Override
    public void handle(HttpExchange ex){
        try {
            if (!"POST".equalsIgnoreCase(ex.getRequestMethod())){
                ex.sendResponseHeaders(405, -1);
                return;
            }

            byte[] body = ex.getRequestBody().readAllBytes();
            Map<String,String> form = Utils.parseForm(new String(body));

            FormRequest req = new FormRequest(UUID.randomUUID().toString(), form);

            if (!server.qIncoming.offer(req)){
                String resp = "Queue full";
                ex.sendResponseHeaders(503, resp.length());
                ex.getResponseBody().write(resp.getBytes());
                ex.close();
                return;
            }

            server.accepted.incrementAndGet();

            String resp = "accepted: " + req.id;
            ex.sendResponseHeaders(200, resp.length());
            ex.getResponseBody().write(resp.getBytes());
            ex.close();

        } catch (Exception e){ e.printStackTrace(); }
    }
}
