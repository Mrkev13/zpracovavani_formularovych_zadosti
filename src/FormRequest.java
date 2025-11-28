import java.util.Map;
import java.util.HashMap;

public class FormRequest {
    public final String id;
    public final Map<String,String> data;
    public volatile String generatedPdfPath;

    public FormRequest(String id, Map<String,String> data){
        this.id = id;
        this.data = new HashMap<>(data);
    }
}
