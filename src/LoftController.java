import org.eclipse.paho.client.mqttv3.MqttAsyncClient;

public class LoftController extends LightController {

    public LoftController(MqttAsyncClient client) {
        super(client, "zigbee2mqtt/LoftSwitch/action");

        registerLighttopic("Loft1", "zigbee2mqtt/LoftLight1/set");
        registerLighttopic("Loft2", "zigbee2mqtt/LoftLight2/set");

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
