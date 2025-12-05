import java.util.concurrent.TimeUnit;

public class Sender implements Runnable {
    private final FormProcessingServer server;

    public Sender(FormProcessingServer server) {
        this.server = server;
    }

    @Override
    public void run() {
        while(server.running) {
            try {
                FormRequest req = server.qProcessed.poll(1, TimeUnit.SECONDS);
                if(req == null) continue;
                server.persistence.saveCompleted(req);
                server.emailSender.sendConfirmation(req);
                server.completed.incrementAndGet();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
