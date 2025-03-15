package smartLights.lightControllers;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Arrays;

public class LoftSecondController extends LightController {

    private LoftController masterController;
    public LoftSecondController(MqttAsyncClient client, LoftController otherLoftController) {
        super(client, "zigbee2mqtt/LoftSwitchSecondary/action");
        masterController = otherLoftController;
    }

    @Override
    public void upLeftSingle() {
        masterController.upLeftSingle();
    }

    @Override
    public void upRightSingle() {
        masterController.upRightSingle();
    }

    @Override
    public void downLeftSingle() {
        masterController.downLeftSingle();
    }

    @Override
    public void downRightSingle() {
        masterController.downRightSingle();
    }

    @Override
    public void upLeftDouble() {
        masterController.upLeftDouble();
    }

    @Override
    public void upRightDouble() {
        masterController.upRightDouble();
    }

    @Override
    public void downLeftDouble() {
        masterController.downLeftDouble();
    }

    @Override
    public void downRightDouble() {
        masterController.downRightDouble();
    }

    @Override
    public void upLeftLong() {
        masterController.upLeftLong();
    }

    @Override
    public void upRightLong() {
        masterController.upRightLong();
    }

    @Override
    public void downLeftLong() {
        masterController.downLeftLong();
    }

    @Override
    public void downRightLong() {
        masterController.downRightLong();
    }

}
