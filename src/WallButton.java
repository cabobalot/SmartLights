import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class WallButton implements IMqttMessageListener {
    MqttAsyncClient client;
    public WallButton(MqttAsyncClient client) {
        this.client = client;
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        System.out.println(s + " : " + mqttMessage);

    }
}
