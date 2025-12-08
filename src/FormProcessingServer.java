import com.sun.net.httpserver.HttpServer;

import javax.xml.validation.Validator;
import java.net.InetSocketAddress;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class FormProcessingServer {

    static final int HTTP_PORT = 8080;

    // Fronty mezi etapami
    public final BlockingQueue<FormRequest> qIncoming = new LinkedBlockingQueue<>(100);
    public final BlockingQueue<FormRequest> qValidated = new LinkedBlockingQueue<>(100);
    public final BlockingQueue<FormRequest> qProcessed = new LinkedBlockingQueue<>(100);

    // Metriky
    public final AtomicLong accepted = new AtomicLong();
    public final AtomicLong validated = new AtomicLong();
    public final AtomicLong processed = new AtomicLong();
    public final AtomicLong completed = new AtomicLong();

    // Workery
    public final ExecutorService validatorPool = Executors.newFixedThreadPool(4);
    public final ExecutorService businessPool = Executors.newFixedThreadPool(4);
    public final ExecutorService pdfPool = Executors.newFixedThreadPool(2);
    public final ExecutorService senderPool = Executors.newFixedThreadPool(2);

    // Semaphore pro PDF
    public final Semaphore pdfSemaphore = new Semaphore(2);

    // Pluggable infra
    public final Persistence persistence;
    public final EmailSender emailSender;

    public volatile boolean running = true;

    public FormProcessingServer(Persistence persistence, EmailSender emailSender) {
        this.persistence = persistence;
        this.emailSender = emailSender;
    }

    public void start() throws Exception {
        // Start workers
        for (int i = 0; i < 4; i++) validatorPool.submit(new FormValidator(this));
        for (int i = 0; i < 4; i++) businessPool.submit(new BusinessProcessor(this));
        for (int i = 0; i < 2; i++) senderPool.submit(new Sender(this));

        // HTTP server
        HttpServer server = HttpServer.create(new InetSocketAddress(HTTP_PORT), 0);
        server.createContext("/", new RootHandler());
        server.createContext("/submit", new SubmitHandler(this));
        server.createContext("/metrics", new MetricsHandler(
                qIncoming, qValidated, qProcessed,
                accepted, validated, processed, completed
        ));
        server.setExecutor(Executors.newFixedThreadPool(2));
        server.start();

        System.out.println("FormProcessingServer started on port " + HTTP_PORT);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            running = false;
            validatorPool.shutdownNow();
            businessPool.shutdownNow();
            pdfPool.shutdownNow();
            senderPool.shutdownNow();
            System.out.println("Server stopped.");
        }));
    }

    public void submitPdfTask(FormRequest req) {
        pdfPool.submit(() -> {
            try {
                pdfSemaphore.acquire();
                Thread.sleep(200); // simulace PDF
                req.generatedPdfPath = "/tmp/" + req.id + ".pdf";
                persistence.saveGenerated(req);
                qProcessed.put(req);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                pdfSemaphore.release();
            }
        });
    }

    public static void main(String[] args) throws Exception {
        InMemoryPersistence persistence = new InMemoryPersistence();
        InMemoryEmailSender emailSender = new InMemoryEmailSender();
        FormProcessingServer server = new FormProcessingServer(persistence, emailSender);
        server.start();
    }
}
