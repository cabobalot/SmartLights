import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Arrays;

public class LoftSecondController extends LightController {

    private LoftController masterController;
    public LoftSecondController(MqttAsyncClient client, LoftController otherLoftController) {
        super(client, "zigbee2mqtt/LoftSwitchSecondary/action");
        masterController = otherLoftController;
    }

    //this feels like the wrong way to do this but...
    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        System.out.println(topic + " : " + mqttMessage);

        if (Arrays.equals(mqttMessage.getPayload(), "1_single".getBytes())) {
            masterController.upLeftSingle();
        } else if (Arrays.equals(mqttMessage.getPayload(), "2_single".getBytes())) {
            masterController.upRightSingle();
        } else if (Arrays.equals(mqttMessage.getPayload(), "3_single".getBytes())) {
            masterController.downLeftSingle();
        } else if (Arrays.equals(mqttMessage.getPayload(), "4_single".getBytes())) {
            masterController.downRightSingle();
        } else if (Arrays.equals(mqttMessage.getPayload(), "1_double".getBytes())) {
            masterController.upLeftDouble();
        } else if (Arrays.equals(mqttMessage.getPayload(), "2_double".getBytes())) {
            masterController.upRightDouble();
        } else if (Arrays.equals(mqttMessage.getPayload(), "3_double".getBytes())) {
            masterController.downLeftDouble();
        } else if (Arrays.equals(mqttMessage.getPayload(), "4_double".getBytes())) {
            masterController.downRightDouble();
        } else if (Arrays.equals(mqttMessage.getPayload(), "1_long".getBytes())) {
            masterController.upLeftLong();
        } else if (Arrays.equals(mqttMessage.getPayload(), "2_long".getBytes())) {
            masterController.upRightLong();
        } else if (Arrays.equals(mqttMessage.getPayload(), "3_long".getBytes())) {
            masterController.downLeftLong();
        } else if (Arrays.equals(mqttMessage.getPayload(), "4_long".getBytes())) {
            masterController.downRightLong();
        }
    }
}
