import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class Utils {
    public static Map<String,String> parseForm(String raw) {
        Map<String,String> map = new HashMap<>();
        if(raw == null || raw.isEmpty()) return map;
        for(String p : raw.split("&")) {
            int eq = p.indexOf('=');
            if(eq < 0) continue;
            try {
                map.put(URLDecoder.decode(p.substring(0, eq), "UTF-8"),
                        URLDecoder.decode(p.substring(eq + 1), "UTF-8"));
            } catch(Exception ignored) {}
        }
        return map;
    }
}
