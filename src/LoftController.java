import org.eclipse.paho.client.mqttv3.MqttAsyncClient;

public class LoftController extends LightController {

    public LoftController(MqttAsyncClient client) {
        super(client, "zigbee2mqtt/LoftSwitch/action");

        registerTopic("Loft1", "zigbee2mqtt/LoftLight1/set");
        registerTopic("Loft2", "zigbee2mqtt/LoftLight2/set");

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
