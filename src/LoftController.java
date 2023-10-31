import org.eclipse.paho.client.mqttv3.MqttAsyncClient;

public class LoftController extends LightController {

    public LoftController(MqttAsyncClient client) {
        super(client, "zigbee2mqtt/LoftSwitch/action");

        registerLightTopic("Loft1", "zigbee2mqtt/LoftLight1/set");
        registerLightTopic("Loft2", "zigbee2mqtt/LoftLight2/set");

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
    public void upLeftLong() {
        HouseStructure.wholeHouseOff();
        HouseStructure.setNightMode();
    }

    @Override
    public void downLeftSingle() {
        HouseStructure.exitNightMode();
    }
}
