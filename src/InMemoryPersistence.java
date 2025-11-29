import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryPersistence implements Persistence {

    private final Set<String> seen = ConcurrentHashMap.newKeySet();

    @Override
    public void saveFailed(FormRequest req, String reason) {
        System.out.println("FAILED: " + req.id + " reason=" + reason);
    }

    @Override
    public boolean isDuplicate(FormRequest req) {
        return !seen.add(req.data.getOrDefault("email", ""));
    }

    @Override
    public void saveGenerated(FormRequest req) {
        System.out.println("PDF generated for " + req.id);
    }

    @Override
    public void saveCompleted(FormRequest req) {
        System.out.println("Completed: " + req.id);
    }
}
