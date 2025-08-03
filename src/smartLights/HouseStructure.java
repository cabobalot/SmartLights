package smartLights;
import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;

import smartLights.lightControllers.BathController;
import smartLights.lightControllers.KitchenController;
import smartLights.lightControllers.LoftController;
import smartLights.lightControllers.LoftSecondController;
import smartLights.lightControllers.LoungeController;
import smartLights.lightControllers.StudyController;

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
    @SuppressWarnings("unused")
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
    private static boolean hasSunriseHappened = true;

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

        timer.scheduleAtFixedRate(CCTTask, 0, 1000*60*15); // every 15 minutes
        

        isInitialized = true;
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

    public static void doSunrise() {
        System.out.println("running sunrise");
        setNightMode();
        lounge.setSunriseMode();
        kitchen.setSunriseMode();
        loft.setSunriseMode();
        bath.setSunriseMode();
        study.setSunriseMode();

        for(int i = 0; i < 60; i++) {
            System.out.println("minute" + i);

            lounge.setSunriseMinute(i);
            kitchen.setSunriseMinute(i);
            loft.setSunriseMinute(i);
            bath.setSunriseMinute(i);
            study.setSunriseMinute(i);

            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        exitNightMode();
        System.out.println("sunrise done");
    } 

    public static void CCTControl() {
        Calendar now = Calendar.getInstance();
        Calendar sunrise = sunCalculator.getCivilSunriseCalendarForDate(now);
        Calendar sunset = sunCalculator.getCivilSunsetCalendarForDate(now);

        double rise;
        Boolean AutoCalculateSunrise = false;
        if (AutoCalculateSunrise) {
            rise = sunrise.get(Calendar.HOUR_OF_DAY) + (sunrise.get(Calendar.MINUTE) / 60.0);
        }
        else {
            rise = 7.5;
        }
        
        double set = sunset.get(Calendar.HOUR_OF_DAY) + (sunset.get(Calendar.MINUTE) / 60.0);
        double noon = (rise / 2) + (set / 2);
        double time = now.get(Calendar.HOUR_OF_DAY) + (now.get(Calendar.MINUTE) / 60.0);

        double A = 350 / Math.pow(rise - noon, 4);

        int newTemp = (int)(A * Math.pow(time - noon, 4)) + 150;

        lounge.setCCT(newTemp);
        kitchen.setCCT(newTemp);
        loft.setCCT(newTemp);
        bath.setCCT(newTemp);
        study.setCCT(newTemp);

        
        // exit night mode on sunrise
        if (hasSunriseHappened) {
            if (time < rise) { // still morning, flag not reset yet
                hasSunriseHappened = false;
            }
        }
        else {
            if (time > rise) { // it is time to sunrise
                // exitNightMode();
                Thread sun = new Thread(HouseStructure::doSunrise);
                sun.start();

                hasSunriseHappened = true;
            }
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

        double CCT = A * Math.pow(time - noon, 4) + 150;

        System.out.println("CCT:" + (int)CCT);

        System.out.println("sunrise: " + sunrise.get(Calendar.HOUR_OF_DAY) + ":" + sunrise.get(Calendar.MINUTE));
        System.out.println("sunset: " + sunset.get(Calendar.HOUR_OF_DAY)  + ":" + sunset.get(Calendar.MINUTE));


    }

    public static LightState getNthSunriseState(int N, int minute) {
        int step = Math.max(minute - (N * 2), 0);
        LightState state = new LightState(step * 4 - 3, (step * 3 + 240) % 360, (int) (100 - (Math.pow(step, 2) / 35)));
        System.out.println("sunrise state:" + state.getFullString());

        return state;
    }

}
