import org.eclipse.paho.client.mqttv3.MqttAsyncClient;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class HouseStructure {
    public static HashMap<String, String> lightTopics = new HashMap<>();
    private static boolean isInitialized = false;
    private static LoungeController lounge;
    private static KitchenController kitchen;
    private static LoftController loft;
    private static LoftSecondController loft2;
    private static BathController bath;
    private static StudyController study;

    private static Timer timer = new Timer();
    private static TimerTask hourlyTask = new TimerTask () {
        @Override
        public void run () {
            CCTControl();
        }
    };

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

        timer.scheduleAtFixedRate(hourlyTask, 0, 1000*60*60);

    }

    public static void wholeHouseOff() {
        lounge.turnOff();
        kitchen.turnOff();
        loft.turnOff();
        bath.turnOff();
        study.turnOff();
    }

    public static void CCTControl() {
        int hour = java.time.LocalTime.now().getHour();
        int newTemp = 10 * (int)Math.pow(hour - 12, 2) + 150;

        //TODO set all the controllers CCT
    }

}
