import java.util.HashMap;

public class HouseStructure {
    public static HashMap<String, String> lightTopics = new HashMap<>();
    private static boolean isInitialized = false;

    public static void init() {
        if (isInitialized) {
            return;
        }

        lightTopics.put("zigbee2mqtt/lounge1/set", "test");

        isInitialized = true;
    }
}
