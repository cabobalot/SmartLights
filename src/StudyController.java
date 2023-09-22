import org.eclipse.paho.client.mqttv3.MqttAsyncClient;

public class StudyController extends LightController {
    public StudyController(MqttAsyncClient client) {
        super(client, "zigbee2mqtt/StudySwitch/action");

        registerTopic("Study1", "zigbee2mqtt/StudyLight1/set");
        registerTopic("Study2", "zigbee2mqtt/StudyLight2/set");
        registerTopic("Study3", "zigbee2mqtt/StudyLight3/set");
        registerTopic("Study4", "zigbee2mqtt/StudyLight4/set");

        try {
            Thread.sleep(500);
            lightToggle();
            Thread.sleep(500);
            lightToggle();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
