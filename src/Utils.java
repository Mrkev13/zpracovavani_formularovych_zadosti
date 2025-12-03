import java.util.HashMap;
import java.util.Map;

public class Utils {
    public static Map<String,String> parseForm(String s){
        Map<String,String> map = new HashMap<>();
        String[] parts = s.split("&");
        for (String p : parts){
            String[] kv = p.split("=");
            if (kv.length == 2) map.put(kv[0], kv[1]);
        }
        return map;
    }
}
