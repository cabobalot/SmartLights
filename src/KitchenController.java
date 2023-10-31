import org.eclipse.paho.client.mqttv3.MqttAsyncClient;

public class KitchenController extends LightController {

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

    @Override
    public void downLeftLong() {
        super.downLeftLong();
    }
}
