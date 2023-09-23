import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class BathController extends LightController {

    private String bathFanTopic = "zigbee2mqtt/BathFan/set";
    private boolean fanOn = false;

    public BathController(MqttAsyncClient client) {
        super(client, "zigbee2mqtt/BathSwitch/action");

        registerLighttopic("Bath1", "zigbee2mqtt/BathLight1/set");
        registerLighttopic("Bath2", "zigbee2mqtt/BathLight2/set");
        registerLighttopic("BathVanity", "zigbee2mqtt/BathVanity/set");

        registerRelayTopic("BathFan", bathFanTopic);

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

    @Override
    public void downLeftSingle() {
        super.downLeftSingle();

        //toggle fan
        fanOn = !fanOn;
        String command = String.format("{\"state\": \"%s\"}", fanOn ? "ON" : "OFF");
        MqttMessage message = new MqttMessage(command.getBytes());
        message.setQos(Main.qos);
        try {
            client.publish(bathFanTopic, message);
        } catch (MqttException e) {
            Main.printError(e);
        }

    }
}
