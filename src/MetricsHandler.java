import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

public class MetricsHandler implements HttpHandler {

    private final BlockingQueue<FormRequest> qIncoming;
    private final BlockingQueue<FormRequest> qValidated;
    private final BlockingQueue<FormRequest> qProcessed;

    private final AtomicLong accepted;
    private final AtomicLong validated;
    private final AtomicLong processed;
    private final AtomicLong completed;

    public MetricsHandler(BlockingQueue<FormRequest> qIncoming,
                          BlockingQueue<FormRequest> qValidated,
                          BlockingQueue<FormRequest> qProcessed,
                          AtomicLong accepted,
                          AtomicLong validated,
                          AtomicLong processed,
                          AtomicLong completed) {
        this.qIncoming = qIncoming;
        this.qValidated = qValidated;
        this.qProcessed = qProcessed;
        this.accepted = accepted;
        this.validated = validated;
        this.processed = processed;
        this.completed = completed;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("accepted=").append(accepted.get()).append("\n");
        sb.append("validated=").append(validated.get()).append("\n");
        sb.append("processed=").append(processed.get()).append("\n");
        sb.append("completed=").append(completed.get()).append("\n");
        sb.append("queues: incoming=").append(qIncoming.size())
                .append(", validated=").append(qValidated.size())
                .append(", processed=").append(qProcessed.size()).append("\n");

        byte[] bytes = sb.toString().getBytes();
        exchange.sendResponseHeaders(200, bytes.length);
        try(var os = exchange.getResponseBody()) { os.write(bytes); }
    }
}
