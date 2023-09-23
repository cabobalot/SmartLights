import org.eclipse.paho.client.mqttv3.MqttAsyncClient;

import java.util.HashMap;

public class HouseStructure {
    public static HashMap<String, String> lightTopics = new HashMap<>();
    private static boolean isInitialized = false;
    private static LoungeController lounge;
    private static KitchenController kitchen;
    private static LoftController loft;
    private static LoftSecondController loft2;
    private static BathController bath;
    private static StudyController study;

    public static void init(MqttAsyncClient mqttClient) {
        if (isInitialized) {
            return;
        }

        lounge = new LoungeController(mqttClient);
        kitchen = new KitchenController(mqttClient);
        loft = new LoftController(mqttClient);
        loft2 = new LoftSecondController(mqttClient, loft);
        bath = new BathController(mqttClient);
        study = new StudyController(mqttClient);

        lightTopics.put("zigbee2mqtt/lounge1/set", "test");

        isInitialized = true;
    }

    public static void wholeHouseOff() {
        lounge.turnOff();
        kitchen.turnOff();
        loft.turnOff();
        bath.turnOff();
        study.turnOff();
    }

}
