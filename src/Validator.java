import java.util.concurrent.TimeUnit;

public class Validator implements Runnable {
    private final FormProcessingServer server;

    public Validator(FormProcessingServer server) {
        this.server = server;
    }

    @Override
    public void run() {
        while(server.running) {
            try {
                FormRequest req = server.qIncoming.poll(1, TimeUnit.SECONDS);
                if(req == null) continue;
                if(!req.data.containsKey("name") || !req.data.containsKey("email")) {
                    server.persistence.saveFailed(req, "missing fields");
                    continue;
                }
                server.validated.incrementAndGet();
                server.qValidated.put(req);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
