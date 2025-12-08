import java.util.HashMap;
import java.util.Map;

public class FormRequest {
    public final String id;
    public final Map<String,String> data;
    public volatile String generatedPdfPath;

    public FormRequest(String id, Map<String,String> data) {
        this.id = id;
        this.data = new HashMap<>(data);
    }
}
