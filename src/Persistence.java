public interface Persistence {
    void saveFailed(FormRequest req, String reason);
    boolean isDuplicate(FormRequest req);
    void saveGenerated(FormRequest req);
    void saveCompleted(FormRequest req);
}
