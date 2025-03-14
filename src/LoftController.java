import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

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
        currentDimmer = daylightDimmer;

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
    public void doLowestDim() {
        System.out.print("Loft lowest dim ...");

        //TODO lol
        // try {
        //     // send brightness 0 to loft1
        //     // using the generalLightState for this is probably a bad idea,
        //     // but its reset in the finally block so its fine...
        //     generalLightState.setBrightness(0);
        //     MqttMessage message = new MqttMessage(generalLightState.getFullString().getBytes());
        //     message.setQos(Main.qos);
        //     client.publish(lightTopics.get("Loft1") , message);

        //     // send brightness 5 to loft2
        //     generalLightState.setBrightness(5);
        //     message = new MqttMessage(generalLightState.getFullString().getBytes());
        //     message.setQos(Main.qos);
        //     client.publish(lightTopics.get("Loft2") , message);
        // } catch (MqttException e) {
        //     Main.printError(e);
        // } catch (NullPointerException e) {
        //     e.printStackTrace();
        // }
        // finally { // always reset the brightness to 5
        //     generalLightState.setBrightness(5);
        // }

        System.out.println("done");
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
