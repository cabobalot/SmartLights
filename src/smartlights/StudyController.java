package smartlights;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.paho.client.mqttv3.MqttAsyncClient;

public class StudyController extends LightController {
    public StudyController(MqttAsyncClient client) {
        super(client, "zigbee2mqtt/StudySwitch/action");

        registerLightTopic("Study1", "zigbee2mqtt/StudyLight1/set");
        registerLightTopic("Study2", "zigbee2mqtt/StudyLight2/set");
        registerLightTopic("Study3", "zigbee2mqtt/StudyLight3/set");
        registerLightTopic("Study4", "zigbee2mqtt/StudyLight4/set");

        generateDimmers();

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
