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
    private static TimerTask CCTTask = new TimerTask () {
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

        isInitialized = true;

        timer.scheduleAtFixedRate(CCTTask, 0, 1000*60*30); // every half hour

    }

    public static void wholeHouseOff() {
        System.out.print("Turning whole house off...");
        lounge.turnOff();
        kitchen.turnOff();
        loft.turnOff();
        bath.turnOff();
        study.turnOff();
        System.out.println("done");
    }

    public static void setNightMode() {
        System.out.print("set night mode...");
        lounge.setNightMode();
        kitchen.setNightMode();
        loft.setNightMode();
        bath.setNightMode();
        study.setNightMode();
        System.out.println("done");
    }

    public static void exitNightMode() {
        System.out.print("set night mode...");
        lounge.setDayMode();
        kitchen.setDayMode();
        loft.setDayMode();
        bath.setDayMode();
        study.setDayMode();
        System.out.println("done");
    }



    public static void CCTControl() {
       int hour = java.time.LocalTime.now().getHour();
       int newTemp = 10 * (int)Math.pow(hour - 12, 2) + 150;

       lounge.setCCT(newTemp);
       kitchen.setCCT(newTemp);
       loft.setCCT(newTemp);
       bath.setCCT(newTemp);
       study.setCCT(newTemp);
    }

}
