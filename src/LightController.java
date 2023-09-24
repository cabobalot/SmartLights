import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public abstract class LightController implements IMqttMessageListener {
    protected HashMap<String, String> lightTopics = new HashMap<>();
    protected static HashMap<String, String> allLightTopics = new HashMap<>();
    protected static HashMap<String, String> allRelayTopics = new HashMap<>();
    protected MqttAsyncClient client;
    protected LightState generalLightState = new LightState();
    protected boolean isOn = false;

    protected Random random = new Random();

    public LightController(MqttAsyncClient client, String topic) {
        this.client = client;
        try {
            client.subscribe(topic, Main.qos, this);
        } catch (MqttException e) {
            Main.printError(e);
            throw new RuntimeException(e);
        }
    }
    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        System.out.println(topic + " : " + mqttMessage);

        if (Arrays.equals(mqttMessage.getPayload(), "1_single".getBytes())) {
            upLeftSingle();
        } else if (Arrays.equals(mqttMessage.getPayload(), "2_single".getBytes())) {
            upRightSingle();
        } else if (Arrays.equals(mqttMessage.getPayload(), "3_single".getBytes())) {
            downLeftSingle();
        } else if (Arrays.equals(mqttMessage.getPayload(), "4_single".getBytes())) {
            downRightSingle();
        } else if (Arrays.equals(mqttMessage.getPayload(), "1_double".getBytes())) {
            upLeftDouble();
        } else if (Arrays.equals(mqttMessage.getPayload(), "2_double".getBytes())) {
            upRightDouble();
        } else if (Arrays.equals(mqttMessage.getPayload(), "3_double".getBytes())) {
            downLeftDouble();
        } else if (Arrays.equals(mqttMessage.getPayload(), "4_double".getBytes())) {
            downRightDouble();
        } else if (Arrays.equals(mqttMessage.getPayload(), "1_long".getBytes())) {
            upLeftLong();
        } else if (Arrays.equals(mqttMessage.getPayload(), "2_long".getBytes())) {
            upRightLong();
        } else if (Arrays.equals(mqttMessage.getPayload(), "3_long".getBytes())) {
            downLeftLong();
        } else if (Arrays.equals(mqttMessage.getPayload(), "4_long".getBytes())) {
            downRightLong();
        }
    }

    public void dimUp() {
        if (!isOn) {
            return;
        }
        generalLightState.setBrightness(generalLightState.getBrightness() + 20);
        System.out.print("Dim up - " + generalLightState.getBrightness() + "...");
        broadcastAll(generalLightState.getFullString());
        System.out.println("done");
    }

    public void dimDown() {
        if (!isOn) {
            return;
        }
        generalLightState.setBrightness(generalLightState.getBrightness() - 20);
        if (generalLightState.getBrightness() < 10) {
            generalLightState.setBrightness(5);
        }
        System.out.print("Dim down - " + generalLightState.getBrightness() + "...");
        broadcastAll(generalLightState.getFullString());
        System.out.println("done");
    }

    public void setBrightness(int brightness) {
        generalLightState.setBrightness(brightness);
        System.out.print("Brightness set - " + generalLightState.getBrightness() + "...");
        broadcastAll(generalLightState.getFullString());
        System.out.println("done");
    }

    /**
     * @deprecated
     */
    public void lightToggle() {
        if (isOn) {
            generalLightState.setBrightness(0);
            System.out.print("Light off ...");
            broadcastAll(generalLightState.getFullString());
            System.out.println("done");

            isOn = false;
        }
        else {
            generalLightState.setMode(LightState.Mode.CCT);
            generalLightState.setBrightness(80);
            System.out.print("Light on ...");
            broadcastAll(generalLightState.getFullString());
            System.out.println("done");

            isOn = true;
        }
    }

    public void turnOn() {
        generalLightState.setMode(LightState.Mode.CCT);
        generalLightState.setBrightness(80);
        System.out.print("Light on ...");
        broadcastAll(generalLightState.getFullString());
        System.out.println("done");
    }

    public void turnOff() {
        generalLightState.setBrightness(0);
        System.out.print("Light off ...");
        broadcastAll(generalLightState.getFullString());
        System.out.println("done");
    }

    public void broadcastAll(String command) {
        lightTopics.forEach((String name, String topic) -> {
            MqttMessage message = new MqttMessage(command.getBytes());
            message.setQos(Main.qos);
            try {
                client.publish(topic, message);
            } catch (MqttException e) {
                Main.printError(e);
            }
        });
    }

    public void startColorMode() {
        generalLightState.setMode(LightState.Mode.COLOR);
        generalLightState.setHue(random.nextInt(360));
        generalLightState.setSaturation(70);
        if (isOn) {
            System.out.print("color " + generalLightState.getHue() + " ...");
            broadcastAll(generalLightState.getFullString());
            System.out.println("done");
        }
        else {
            setBrightness(5);
            isOn = true;
        }

    }

    public void stopColorMode() {
        System.out.print("color off ...");
        generalLightState.setMode(LightState.Mode.CCT);
        broadcastAll(generalLightState.getFullString());
        System.out.println("done");
    }

    public void setCCT(int cct) {
        generalLightState.setColorTemp(cct);
    }

    public void upLeftSingle() {
        if (isOn) {
            turnOff();
            isOn = false;
        }
        else {
            turnOn();
            isOn = true;
        }
    }

    public void upRightSingle() {
        dimUp();
    }

    public void downLeftSingle() {

    }

    public void downRightSingle() {
        if (isOn) {
            dimDown();
        }
        else {
            setBrightness(5);
            isOn = true;
        }
    }

    public void upLeftDouble() {
        if (generalLightState.getMode() != LightState.Mode.COLOR) {
            startColorMode();
        } else {
            stopColorMode();
        }
    }

    public void upRightDouble() {
        setBrightness(100);
    }

    public void downLeftDouble() {

    }

    public void downRightDouble() {
        if (isOn){
            setBrightness(5);
        }
    }

    public void upLeftLong() {

    }

    public void upRightLong() {

    }

    public void downLeftLong() {

    }

    public void downRightLong() {

    }

    public void registerLighttopic(String name, String topic) {
        lightTopics.put(name, topic);
        allLightTopics.put(name, topic);
    }

    public void registerRelayTopic(String name, String topic) {
        allRelayTopics.put(name, topic);
    }
}
