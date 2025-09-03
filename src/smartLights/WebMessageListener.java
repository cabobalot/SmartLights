package smartLights;

import java.util.Arrays;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class WebMessageListener implements IMqttMessageListener {

	private MqttAsyncClient client;
	private final String topicString = "zigbee2mqtt/smartLights/settings";

	private double sunriseTime = 7.5;

	public WebMessageListener(MqttAsyncClient client) {
		this.client = client;
        try {
            client.subscribe(topicString + "/#", Main.qos, this);
        } catch (MqttException e) {
            Main.printError(e);
            throw new RuntimeException(e);
        }
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
        System.out.println(topic + " : " + message);
		
		if (topic.endsWith("get")) {
			MqttMessage response = new MqttMessage(("sunriseTime:" + sunriseTime).getBytes());
            client.publish(topicString, response);
		}

		if (topic.endsWith("set/sunriseTime")) {
			try {
				sunriseTime = Integer.parseInt(new String(message.getPayload()));
				System.out.println("new time:" + sunriseTime);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public double getSunriseTime() {
		return sunriseTime;
	}

	public static void main(String[] args) {
		String broker = "tcp://192.168.1.25:1883";
    	final String clientID = "SmartLightJavaController";
		try {
            MqttAsyncClient mqttClient = new MqttAsyncClient(broker, clientID, new MemoryPersistence());

            System.out.println("Connecting to broker at: " + broker);
            mqttClient.connect();
            while (!mqttClient.isConnected()); // wait for connection
            System.out.println("Connection successful");

            MqttMessage message = new MqttMessage("Hello".getBytes());
            mqttClient.publish("zigbee2mqtt", message);

			WebMessageListener listener = new WebMessageListener(mqttClient);

			while (true) {
			}

		} catch (MqttException e) {
            e.printStackTrace();
        }
	}


}