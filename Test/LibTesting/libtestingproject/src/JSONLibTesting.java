import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

public class JSONLibTesting {
    public static void main(String[] args) {
        String settings = "{\"hello\": \"world\"}";

        JSONObject obj = (JSONObject) JSONSerializer.toJSON(settings);
        System.out.println(obj.toString());
    }
}
