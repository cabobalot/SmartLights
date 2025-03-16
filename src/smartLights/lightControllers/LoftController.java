package smartLights.lightControllers;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;

import smartLights.HouseStructure;
import smartLights.LightState;
import smartLights.RoomState;
import smartLights.dimmers.ColorDimmer;
import smartLights.dimmers.DaylightDimmer;
import smartLights.dimmers.Dimmer;
import smartLights.dimmers.ListDimmer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LoftController extends LightController {

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
        colorDimmer = new ColorDimmer(lightNames, brightnesses);

        // create a list of red color room states
        List<RoomState> roomStates = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			RoomState room = new RoomState();
			// create one state object and duplicate the pointer for every light
			LightState state = new LightState(Dimmer.niceBrightnesses[i], 0, 100);
			for (String name : lightNames) {
                if (i == 0 && name.equals("Loft1")) {
                    room.lightStates.put(name, new LightState(0, 0,100)); // one light off at lowest dim
                    continue;
                }
				room.lightStates.put(name, state);
			}
			roomStates.add(room);
		}
        nightDimmer = new ListDimmer(roomStates, 0);


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
