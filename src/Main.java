import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class Main {

    public static String broker = "tcp://192.168.1.20:1883";
    public static final String clientID = "SmartLightJavaController";
    public static final int qos = 1;

    public static void main(String[] args) {
        System.out.println("Light controller starting");
        if (args.length != 0) {
            broker = args[0];
        }
        else {
            System.out.println("Using default broker");
        }
        System.out.println("Broker: " + broker);

        try {
            MqttAsyncClient mqttClient = new MqttAsyncClient(broker, clientID, new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setMaxInflight(100);
            options.setCleanSession(true);

            System.out.println("Connecting to broker at: " + broker);
            mqttClient.connect();
            while (!mqttClient.isConnected()); // wait for connection
            System.out.println("Connection successful");

            MqttMessage message = new MqttMessage("Hello".getBytes());
            message.setQos(qos);
            mqttClient.publish("zigbee2mqtt", message);

            HouseStructure.init(mqttClient);


            while(true);

//            System.out.println("controller running, enter 'q' to close program");
//            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//            String input;
//            try {
//                do {
//                    input = reader.readLine();
//                } while (!input.equals("q"));
//            } catch (IOException e) {
//                System.out.println("Error reading input");
//                e.printStackTrace();
//            }

//            System.out.println("Closing connection");
//            mqttClient.disconnect();

        } catch (MqttException e) {
            printError(e);
        }
    }

    public static void printError(MqttException e) {
        System.out.println("ERROR");
        System.out.println("reason " + e.getReasonCode());
        System.out.println("msg " + e.getMessage());
        System.out.println("loc " + e.getLocalizedMessage());
        System.out.println("cause " + e.getCause());
        System.out.println("excep " + e);
        e.printStackTrace();
    }
}