import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.HashMap;

public class LightController implements IMqttMessageListener {
    public static HashMap<String, String> lightTopics = new HashMap<>();
    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {

    }
}
