import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

public class MetricsHandler implements HttpHandler {

    private final FormProcessingServer server;

    public MetricsHandler(FormProcessingServer server){
        this.server = server;
    }

    @Override
    public void handle(HttpExchange ex){
        try {
            String resp =
                    "accepted=" + server.accepted.get() + "\n" +
                            "validated=" + server.validated.get() + "\n" +
                            "processed=" + server.processed.get() + "\n" +
                            "completed=" + server.completed.get() + "\n" +
                            "queues: incoming=" + server.qIncoming.size() +
                            ", validated=" + server.qValidated.size() +
                            ", processed=" + server.qProcessed.size() + "\n";

            ex.sendResponseHeaders(200, resp.getBytes().length);
            ex.getResponseBody().write(resp.getBytes());
            ex.getResponseBody().close();
        } catch (Exception e){ e.printStackTrace(); }
    }
}
