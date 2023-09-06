import org.eclipse.paho.client.mqttv3.MqttAsyncClient;


public class LoungeController extends LightController {

    //mosquitto_pub -h 192.168.1.20 -t "zigbee2mqtt/LoftSwitch/action" -m "1_single"
    public LoungeController(MqttAsyncClient client) {
        super(client, "zigbee2mqtt/LoungeSwitch/action");

        registerTopic("lounge1", "zigbee2mqtt/LoungeLight1/set");
        registerTopic("lounge2", "zigbee2mqtt/LoungeLight2/set");
        registerTopic("lounge3", "zigbee2mqtt/LoungeLight3/set");

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
