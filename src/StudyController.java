import org.eclipse.paho.client.mqttv3.MqttAsyncClient;

public class StudyController extends LightController {
    public StudyController(MqttAsyncClient client) {
        super(client, "zigbee2mqtt/StudySwitch/action");

        registerLighttopic("Study1", "zigbee2mqtt/StudyLight1/set");
        registerLighttopic("Study2", "zigbee2mqtt/StudyLight2/set");
        registerLighttopic("Study3", "zigbee2mqtt/StudyLight3/set");
        registerLighttopic("Study4", "zigbee2mqtt/StudyLight4/set");

        try {
            Thread.sleep(500);
            turnOff();
            Thread.sleep(500);
            turnOn();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
