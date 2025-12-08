import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class SubmitHandler implements HttpHandler {
    private final FormProcessingServer server;

    public SubmitHandler(FormProcessingServer server) {
        this.server = server;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        if(!"POST".equalsIgnoreCase(ex.getRequestMethod())) {
            ex.sendResponseHeaders(405, -1);
            return;
        }
        byte[] body = ex.getRequestBody().readAllBytes();
        Map<String,String> form = Utils.parseForm(new String(body));
        FormRequest req = new FormRequest(UUID.randomUUID().toString(), form);

        if(!server.qIncoming.offer(req)) {
            String resp = "Queue full, try again later";
            ex.sendResponseHeaders(503, resp.getBytes().length);
            try(var os = ex.getResponseBody()) { os.write(resp.getBytes()); }
            return;
        }

        server.accepted.incrementAndGet();
        String resp = "accepted: " + req.id;
        ex.sendResponseHeaders(200, resp.getBytes().length);
        try(var os = ex.getResponseBody()) { os.write(resp.getBytes()); }
    }
}
