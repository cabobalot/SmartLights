package smartLights.lightControllers;

import java.util.List;

import org.eclipse.paho.client.mqttv3.MqttAsyncClient;

import smartLights.dimmers.SunriseDimmer;

public class StudyController extends LightController {
    public StudyController(MqttAsyncClient client) {
        super(client, "zigbee2mqtt/StudySwitch/action");

        registerLightTopic("Study1", "zigbee2mqtt/StudyLight1/set");
        registerLightTopic("Study2", "zigbee2mqtt/StudyLight2/set");
        registerLightTopic("Study3", "zigbee2mqtt/StudyLight3/set");
        registerLightTopic("Study4", "zigbee2mqtt/StudyLight4/set");

        generateDimmers();

        sunriseDimmer = new SunriseDimmer(List.of("Study1", "Study2", "Study3", "Study4"), 5);

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
