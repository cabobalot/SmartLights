import org.eclipse.paho.client.mqttv3.MqttAsyncClient;

public class KitchenController extends LightController {

    public KitchenController(MqttAsyncClient client) {
        super(client, "zigbee2mqtt/KitchenSwitch/action");

        registerLighttopic("Kitchen1", "zigbee2mqtt/KitchenLight1/set");
        registerLighttopic("Kitchen2", "zigbee2mqtt/KitchenLight2/set");
        registerLighttopic("Kitchen3", "zigbee2mqtt/KitchenLight3/set");
        registerLighttopic("Kitchen4", "zigbee2mqtt/KitchenLight4/set");
        registerLighttopic("Kitchen5", "zigbee2mqtt/KitchenLight5/set");

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
}
