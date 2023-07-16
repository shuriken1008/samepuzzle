package samepuzzle;
import java.util.HashMap;
import java.util.Map;

public class Json {
    public static void main(String[] args) {
        String json = "{\"name\":\"John\", \"age\":30, \"city\":\"New York\"}";

        HashMap<String, Object> hashMap = toHashMap(json);

        // HashMapの内容を表示
        for (Map.Entry<String, Object> entry : hashMap.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }


        String jsonString = toJson(hashMap);
        System.out.println(jsonString);

    }

    public static HashMap<String, Object> toHashMap(String json) {
        HashMap<String, Object> hashMap = new HashMap<>();

        // JSON文字列から要素を抽出してHashMapに追加
        if(!json.matches("\\{.*\\}")){
            return null;
        }
        String[] keyValuePairs = json.replaceAll("[{}\"]", "").split(",");
        for (String pair : keyValuePairs) {
            String[] entry = pair.split(":");
            String key = entry[0].trim();
            String value = entry[1].trim();
            hashMap.put(key, value);
        }

        return hashMap;
    }

    public static String toJson(Map<String, Object> data) {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");

        boolean isFirst = true;
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (!isFirst) {
                jsonBuilder.append(",");
            } else {
                isFirst = false;
            }

            String key = entry.getKey();
            Object value = entry.getValue();

            jsonBuilder.append("\"").append(key).append("\":");
            if (value instanceof String) {
                jsonBuilder.append("\"").append(value).append("\"");
            } else {
                jsonBuilder.append(value);
            }
        }

        jsonBuilder.append("}");
        return jsonBuilder.toString();
    }
}
