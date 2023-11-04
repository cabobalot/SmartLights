import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;

import java.util.Calendar;
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
    private static Location location = new Location("41.0", "-112.0");
     private static SunriseSunsetCalculator sunCalculator = new SunriseSunsetCalculator(location, "America/Denver");

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
        System.out.print("exit night mode...");
        lounge.setDayMode();
        kitchen.setDayMode();
        loft.setDayMode();
        bath.setDayMode();
        study.setDayMode();
        System.out.println("done");
    }



    public static void CCTControl() {
        Calendar now = Calendar.getInstance();
        Calendar sunrise = sunCalculator.getCivilSunriseCalendarForDate(now);
        Calendar sunset = sunCalculator.getCivilSunsetCalendarForDate(now);

        double rise = sunrise.get(Calendar.HOUR_OF_DAY) + (sunrise.get(Calendar.MINUTE) / 60.0);
        double set = sunset.get(Calendar.HOUR_OF_DAY) + (sunset.get(Calendar.MINUTE) / 60.0);
        double noon = (rise / 2) + (set / 2);
        double time = now.get(Calendar.HOUR_OF_DAY) + (now.get(Calendar.MINUTE) / 60.0);

        double A = 350 / Math.pow(rise - noon, 4);

        int newTemp = (int)(A * Math.pow(time - noon, 4));

        lounge.setCCT(newTemp);
        kitchen.setCCT(newTemp);
        loft.setCCT(newTemp);
        bath.setCCT(newTemp);
        study.setCCT(newTemp);

        // exit night mode on sunrise
        if (time > rise) {
            exitNightMode();
        }
    }

    public static void main(String[] args) {
        Calendar now = Calendar.getInstance();
        Calendar sunrise = sunCalculator.getCivilSunriseCalendarForDate(now);
        Calendar sunset = sunCalculator.getCivilSunsetCalendarForDate(now);

        double rise = sunrise.get(Calendar.HOUR_OF_DAY) + (sunrise.get(Calendar.MINUTE) / 60.0);
        double set = sunset.get(Calendar.HOUR_OF_DAY) + (sunset.get(Calendar.MINUTE) / 60.0);
        double noon = (rise / 2) + (set / 2);
        double time = now.get(Calendar.HOUR_OF_DAY) + (now.get(Calendar.MINUTE) / 60.0);

        double A = 350 / Math.pow(rise - noon, 4);

        double CCT = A * Math.pow(time - noon, 4);

        System.out.println("CCT:" + (int)CCT);

        System.out.println("sunrise: " + sunrise.get(Calendar.HOUR_OF_DAY) + ":" + sunrise.get(Calendar.MINUTE));
        System.out.println("sunset: " + sunset.get(Calendar.HOUR_OF_DAY)  + ":" + sunset.get(Calendar.MINUTE));


    }

}
