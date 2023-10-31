import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Random;

public class KitchenController extends LightController {

    private Thread discoThread;
    private boolean discoRunning = false;

    public KitchenController(MqttAsyncClient client) {
        super(client, "zigbee2mqtt/KitchenSwitch/action");

        registerLightTopic("Kitchen1", "zigbee2mqtt/KitchenLight1/set");
        registerLightTopic("Kitchen2", "zigbee2mqtt/KitchenLight2/set");
        registerLightTopic("Kitchen3", "zigbee2mqtt/KitchenLight3/set");
        registerLightTopic("Kitchen4", "zigbee2mqtt/KitchenLight4/set");
        registerLightTopic("Kitchen5", "zigbee2mqtt/KitchenLight5/set");
        registerLightTopic("KitchenCabinet", "zigbee2mqtt/KitchenCabinet/set");

        try {
            Thread.sleep(500);
            turnOn();
            Thread.sleep(500);
            turnOff();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //disco...
    @Override
    public void downLeftLong() {
        if (discoRunning) {
            discoRunning = false;
            System.out.println("Disco mode turned off :(");
        }
        else {
            discoRunning = true;
            discoThread = new Thread(this::doTheDisco);
            discoThread.start();
            System.out.println("Disco mode on :)");
        }

    }

    private void doTheDisco() {
        while (discoRunning) { // pick random colors every second for every light
            allLightTopics.forEach((String name, String topic) -> {
                String message = String.format("""
                    {"color":{"hue":%d,"saturation":%d}, "brightness":%d}""", random.nextInt(6) * 60, 100, 80);
                broadcast(message, topic);
            });
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

        }
    }
}
