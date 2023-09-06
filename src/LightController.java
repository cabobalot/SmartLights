import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Arrays;
import java.util.HashMap;

public abstract class LightController implements IMqttMessageListener {
    protected HashMap<String, String> lightTopics = new HashMap<>();
    protected static HashMap<String, String> allLightTopics = new HashMap<>();
    protected MqttAsyncClient client;
    protected LightState generalLightState = new LightState();
    protected boolean isOn = false;

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

    protected void dimUp() {
        if (!isOn) {
            return;
        }
        generalLightState.setBrightness(generalLightState.getBrightness() + 20);
        System.out.print("Dim up - " + generalLightState.getBrightness() + "...");
        broadcastAll("{" + generalLightState.getBrightnessString() + "}");
        System.out.println("done");
    }

    protected void dimDown() {
        if (!isOn) {
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

    protected void setBrightness(int brightness) {
        generalLightState.setBrightness(brightness);
        System.out.print("Brightness set - " + generalLightState.getBrightness() + "...");
        broadcastAll(String.format("{%s, %s}", generalLightState.getBrightnessString(), generalLightState.getColorTempString()));
        System.out.println("done");
    }

    protected void lightToggle() {
        if (isOn) {
            generalLightState.setBrightness(0);
            System.out.print("Light off ...");
            broadcastAll("{" + generalLightState.getBrightnessString() + "}");
            System.out.println("done");

            isOn = false;
        }
        else {
            generalLightState.setBrightness(80);
            System.out.print("Light on ...");
            broadcastAll(String.format("{%s, %s}", generalLightState.getBrightnessString(), generalLightState.getColorTempString()));
            System.out.println("done");

            isOn = true;
        }
    }

    protected void broadcastAll(String command) {
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

    protected void upLeftSingle() {
        lightToggle();
    }

    protected void upRightSingle() {
        dimUp();
    }

    protected void downLeftSingle() {

    }

    protected void downRightSingle() {
        if (isOn) {
            dimDown();
        }
        else {
            setBrightness(5);
            isOn = true;
        }
    }

    protected void upLeftDouble() {

    }

    protected void upRightDouble() {
        setBrightness(100);
    }

    protected void downLeftDouble() {

    }

    protected void downRightDouble() {
        if (isOn){
            setBrightness(5);
        }
    }

    protected void upLeftLong() {

    }

    protected void upRightLong() {

    }

    protected void downLeftLong() {

    }

    protected void downRightLong() {

    }

    protected void registerTopic(String name, String topic) {
        lightTopics.put(name, topic);
        allLightTopics.put(name, topic);
    }
}
