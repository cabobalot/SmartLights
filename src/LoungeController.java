import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Arrays;
import java.util.HashMap;

public class LoungeController implements IMqttMessageListener {

    public static HashMap<String, String> lightTopics = new HashMap<>();
    private MqttAsyncClient client;
    private boolean loungeOn = false;
    private LightState generalLightState = new LightState();


    public LoungeController(MqttAsyncClient client) {
        this.client = client;

        generalLightState.setBrightness(80);

        lightTopics.put("lounge1", "zigbee2mqtt/LoungeLight1/set");
        lightTopics.put("lounge2", "zigbee2mqtt/LoungeLight2/set");
        lightTopics.put("lounge3", "zigbee2mqtt/LoungeLight3/set");
        lightTopics.put("kitchen1", "zigbee2mqtt/KitchenLight1/set");
        lightTopics.put("kitchen2", "zigbee2mqtt/KitchenLight2/set");
        lightTopics.put("kitchen3", "zigbee2mqtt/KitchenLight3/set");
        lightTopics.put("kitchen4", "zigbee2mqtt/KitchenLight4/set");
        lightTopics.put("kitchen5", "zigbee2mqtt/KitchenLight5/set");

        try {
            client.subscribe("zigbee2mqtt/loungeSwitch/action", Main.qos, this);
        } catch (MqttException e) {
            Main.printError(e);
            throw new RuntimeException(e);
        }

        try {
            Thread.sleep(500);
            lightToggle();
            Thread.sleep(500);
            lightToggle();
            Thread.sleep(500);
            lightToggle();
            Thread.sleep(500);
            lightToggle();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //mosquitto_pub -h 192.168.1.20 -t "zigbee2mqtt/LoftSwitch/action" -m "1_single"
    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        System.out.println(topic + " : " + mqttMessage);

        if (Arrays.equals(mqttMessage.getPayload(), "1_single".getBytes())) {
            lightToggle();
        } else if (Arrays.equals(mqttMessage.getPayload(), "2_single".getBytes())) {
            dimUp();
        } else if (Arrays.equals(mqttMessage.getPayload(), "3_single".getBytes())) {
            // color mode
        } else if (Arrays.equals(mqttMessage.getPayload(), "4_single".getBytes())) {
            if (loungeOn) {
                dimDown();
            }
            else {
                setBrightness(5);
                loungeOn = true;
            }

        } else if (Arrays.equals(mqttMessage.getPayload(), "2_double".getBytes())) {
            setBrightness(100);
        } else if (Arrays.equals(mqttMessage.getPayload(), "4_double".getBytes())) {
            if (loungeOn){
                setBrightness(5);
            }
        }
    }

    private void dimUp() {
        if (!loungeOn) {
            return;
        }
        generalLightState.setBrightness(generalLightState.getBrightness() + 20);
        System.out.print("Dim up - " + generalLightState.getBrightness() + "...");
        broadcastAll("{" + generalLightState.getBrightnessString() + "}");
        System.out.println("done");
    }

    private void dimDown() {
        if (!loungeOn) {
            return;
        }
        generalLightState.setBrightness(generalLightState.getBrightness() - 20);
        if (generalLightState.getBrightness() < 10) {
            generalLightState.setBrightness(5);
        }
        System.out.print("Dim down - " + generalLightState.getBrightness() + "...");
        broadcastAll("{" + generalLightState.getBrightnessString() + "}");
        System.out.println("done");
    }

    private void setBrightness(int brightness) {
        generalLightState.setBrightness(brightness);
        System.out.print("Brightness set - " + generalLightState.getBrightness() + "...");
        broadcastAll(String.format("{%s, %s}", generalLightState.getBrightnessString(), generalLightState.getColorTempString()));
        System.out.println("done");
    }

    private void lightToggle() {
        if (loungeOn) {
            generalLightState.setBrightness(0);
            System.out.print("Light off ...");
            broadcastAll("{" + generalLightState.getBrightnessString() + "}");
            System.out.println("done");

            loungeOn = false;
        }
        else {
            generalLightState.setBrightness(80);
            System.out.print("Light on ...");
            broadcastAll(String.format("{%s, %s}", generalLightState.getBrightnessString(), generalLightState.getColorTempString()));
            System.out.println("done");

            loungeOn = true;
        }
    }

    private void broadcastAll(String command) {
        lightTopics.forEach((String name, String topic) -> {
            MqttMessage message = new MqttMessage(command.getBytes());
            message.setQos(Main.qos);
            try {
                client.publish(topic, message);
            } catch (MqttException e) {
                Main.printError(e);
            }
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        });
    }
}
