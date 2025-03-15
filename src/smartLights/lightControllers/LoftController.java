package smartLights.lightControllers;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import smartLights.HouseStructure;
import smartLights.dimmers.DaylightDimmer;
import smartLights.dimmers.Dimmer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LoftController extends LightController {

    private int dimVal = 4;

    public LoftController(MqttAsyncClient client) {
        super(client, "zigbee2mqtt/LoftSwitch/action");

        registerLightTopic("Loft1", "zigbee2mqtt/LoftLight1/set");
        registerLightTopic("Loft2", "zigbee2mqtt/LoftLight2/set");

        generateDimmers(); // generate defaults and then replace them below

        Set<String> lightNames = lightTopics.keySet();
        List<Map<String, Integer>> brightnesses = new ArrayList<>();
		for (int i = 0; i < Dimmer.niceBrightnesses.length; i++) {
			Map<String, Integer> toAdd = new HashMap<>();
			for (String name : lightNames) {
                if (i == 0 && name.equals("Loft1")) { //TODO this is an example for generating a complicated brightness profile, but we should use JSON
                    toAdd.put(name, 0);
                    continue;
                }
				toAdd.put(name, Dimmer.niceBrightnesses[i]);
			}
			brightnesses.add(toAdd);
		}
        daylightDimmer = new DaylightDimmer(lightNames, brightnesses);


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
