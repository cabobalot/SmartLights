import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class KitchenController extends LightController {

    public KitchenController(MqttAsyncClient client) {
        super(client, "zigbee2mqtt/KitchenSwitch/action");

        registerTopic("kitchen1", "zigbee2mqtt/KitchenLight1/set");
        registerTopic("kitchen2", "zigbee2mqtt/KitchenLight2/set");
        registerTopic("kitchen3", "zigbee2mqtt/KitchenLight3/set");
        registerTopic("kitchen4", "zigbee2mqtt/KitchenLight4/set");
        registerTopic("kitchen5", "zigbee2mqtt/KitchenLight5/set");

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
