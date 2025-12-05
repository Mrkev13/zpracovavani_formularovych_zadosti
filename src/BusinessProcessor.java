import java.util.concurrent.TimeUnit;

public class BusinessProcessor implements Runnable {
    private final FormProcessingServer server;

    public BusinessProcessor(FormProcessingServer server) {
        this.server = server;
    }

    @Override
    public void run() {
        while(server.running) {
            try {
                FormRequest req = server.qValidated.poll(1, TimeUnit.SECONDS);
                if(req == null) continue;
                if(server.persistence.isDuplicate(req)) {
                    server.persistence.saveFailed(req, "duplicate");
                    continue;
                }
                Thread.sleep(50); // simulace práce
                server.processed.incrementAndGet();
                server.qProcessed.put(req);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
