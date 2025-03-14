package smartlights;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import smartlights.StateMachine.Signal;
import smartlights.StateMachine.State;

import java.util.Arrays;
import java.util.HashMap;

public abstract class LightController implements IMqttMessageListener {
    protected HashMap<String, String> lightTopics = new HashMap<>();
    protected static HashMap<String, String> allLightTopics = new HashMap<>();
    protected static HashMap<String, String> allRelayTopics = new HashMap<>();
    protected MqttAsyncClient client;
    protected boolean isOn = false; // TODO make this a robust state machine

    protected StateMachine stateMachine = new StateMachine();

    protected Dimmer currentDimmer;
    protected DaylightDimmer daylightDimmer;
    protected ListDimmer colorDimmer;
    protected LightState offState = new LightState(0, 0);

    protected SmartRandom random = new SmartRandom(6, 3);

    /**
     * Child classes should define a dimmer
     * @param client
     * @param topic
     */
    public LightController(MqttAsyncClient client, String topic) {
        this.client = client;
        try {
            client.subscribe(topic, Main.qos, this);
        } catch (MqttException e) {
            Main.printError(e);
            throw new RuntimeException(e);
        }

        fillStateMachine();
    }
    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) {
        System.out.println(topic + " : " + mqttMessage);

        if (Arrays.equals(mqttMessage.getPayload(), "1_single".getBytes())) {
            upLeftSingle();
        } else if (Arrays.equals(mqttMessage.getPayload(), "2_single".getBytes())) {
            upRightSingle();
        } else if (Arrays.equals(mqttMessage.getPayload(), "3_single".getBytes())) {
            downLeftSingle();
        } else if (Arrays.equals(mqttMessage.getPayload(), "4_single".getBytes())) {
            downRightSingle();
        } else if (Arrays.equals(mqttMessage.getPayload(), "1_double".getBytes())) {
            upLeftDouble();
        } else if (Arrays.equals(mqttMessage.getPayload(), "2_double".getBytes())) {
            upRightDouble();
        } else if (Arrays.equals(mqttMessage.getPayload(), "3_double".getBytes())) {
            downLeftDouble();
        } else if (Arrays.equals(mqttMessage.getPayload(), "4_double".getBytes())) {
            downRightDouble();
        } else if (Arrays.equals(mqttMessage.getPayload(), "1_hold".getBytes())) {
            upLeftLong();
        } else if (Arrays.equals(mqttMessage.getPayload(), "2_hold".getBytes())) {
            upRightLong();
        } else if (Arrays.equals(mqttMessage.getPayload(), "3_hold".getBytes())) {
            downLeftLong();
        } else if (Arrays.equals(mqttMessage.getPayload(), "4_hold".getBytes())) {
            downRightLong();
        }
    }

    public void dimUp() {
        if (!isOn) {
            return;
        }
		System.out.println("Dim up");
        broadcastRoomState(currentDimmer.dimUp());
    }

    public void dimDown() {
        if (!isOn) {
            return;
        }
		System.out.println("Dim down");
        broadcastRoomState(currentDimmer.dimDown());
    }

    public void turnOn() {
        // TODO state machine
        // generalLightState.setMode(LightState.Mode.CCT); //             if in night mode this set will fail
        // if (generalLightState.getMode() == LightState.Mode.NIGHT) { // and it will stay in night mode
        //     generalLightState.setDimSetting(1);
        // }
        // else {
        //     generalLightState.setDimSetting(4);
        // }
        System.out.print("Light on ...");
        broadcastRoomState(currentDimmer.firstOn());
        System.out.println("done");
        isOn = true;
    }

    public void turnOff() {
        System.out.print("Light off ...");
        broadcastAll(offState.getFullString());
        System.out.println("done");
        isOn = false;
    }

    public void setNightMode() {
        // generalLightState.setMode(LightState.Mode.NIGHT);
    }

    public void setDayMode() {
//         if (generalLightState.exitNightMode() && isOn) { // only send the broadcast if we were in night mode to begin with
//             setBrightness();
// //            broadcastAll(generalLightState.getFullString());
//         }
    }

    /**
     * broadcast the mqtt message for the current state in the current dimmer
     */
    public void broadcastRoomState() {
        broadcastRoomState(currentDimmer.getRoomState());
    }

    /**
     * broadcast the mqtt message for the roomState
     */
    public void broadcastRoomState(RoomState room) {
        room.lightStates.forEach((String name, LightState state) -> {
            MqttMessage message = new MqttMessage(state.getFullString().getBytes());
            message.setQos(Main.qos);
            try {
                String topic = lightTopics.get(name);
                client.publish(topic, message);
            } catch (MqttException e) {
                Main.printError(e);
            }
        });
    }

    public void broadcastAll(String command) {
        lightTopics.forEach((String name, String topic) -> {
            MqttMessage message = new MqttMessage(command.getBytes());
            message.setQos(Main.qos);
            try {
                client.publish(topic, message);
            } catch (MqttException e) {
                Main.printError(e);
            }
        });
    }

    public void broadcast(String command, String topic) {
        MqttMessage message = new MqttMessage(command.getBytes());
        message.setQos(Main.qos);
        try {
            client.publish(topic, message);
        } catch (MqttException e) {
            Main.printError(e);
        }
    }

    public void startColorMode() {
        // generalLightState.setMode(LightState.Mode.COLOR);
        // generalLightState.setHue(random.getRandomInt() * 60); //TODO make this better lol
        // generalLightState.setSaturation(70);
        // if (isOn) {
        //     System.out.print("color " + generalLightState.getHue() + " ...");
        //     broadcastAll(generalLightState.getFullString());
        //     System.out.println("done");
        // }
        // else {
        //     generalLightState.setDimSetting(1);
        //     setBrightness();
        //     isOn = true;
        // }

    }

    public void stopColorMode() {
        // System.out.print("color off ...");
        // generalLightState.setMode(LightState.Mode.CCT);
        // broadcastAll(generalLightState.getFullString());
        // System.out.println("done");
    }

    protected void enterOFF(Signal s) {
	}

	protected void enterCCT(Signal s) {
	}

	protected void enterColor(Signal s) {
	}

	protected void enterNightOff(Signal s) {
	}

	protected void enterNightOn(Signal s) {
	}

	protected void enterNightCCT(Signal s) {
	}

	protected void enterNightColor(Signal s) {
	}

    public void setCCT(int cct) {
        daylightDimmer.setCCT(cct);

        //TODO only broadcast if state is not off
        broadcastRoomState(); // will only change cct if the current dimmer is the daylight dimmer
    }

    public void upLeftSingle() {
        if (isOn) {
            turnOff();
            isOn = false;
        }
        else {
            turnOn();
            isOn = true;
        }
    }

    public void upRightSingle() {
        dimUp();
    }

    public void downLeftSingle() {

    }

    public void downRightSingle() {
        if (isOn) {
            dimDown();
        }
        else {
            // generalLightState.setMode(LightState.Mode.CCT);
            // generalLightState.setDimSetting(1);
            // setBrightness();
            // isOn = true;
        }
    }

    public void upLeftDouble() {
        // if (generalLightState.getMode() != LightState.Mode.COLOR) {
        //     startColorMode();
        // } else {
        //     stopColorMode();
        // }
    }

    public void upRightDouble() {
        broadcastRoomState(currentDimmer.dimMax());
    }

    public void downLeftDouble() {

    }

    public void downRightDouble() {
        if (isOn){
            broadcastRoomState(currentDimmer.dimMin());
        }
    }

    public void upLeftLong() {

    }

    public void upRightLong() {

    }

    public void downLeftLong() {

    }

    public void downRightLong() {

    }

    public void registerLightTopic(String name, String topic) {
        lightTopics.put(name, topic);
        allLightTopics.put(name, topic);
    }

    public void registerRelayTopic(String name, String topic) {
        allRelayTopics.put(name, topic);
    }

    /**
     * helper to create the default dimmers. only do this
     * after registering the light topics.
     */
    protected void generateDimmers() {
        daylightDimmer = new DaylightDimmer(lightTopics.keySet());

        currentDimmer = daylightDimmer;
    }

    protected void fillStateMachine() {
        // im so sorry for this. type saftey requires using the Signal 
        // and State types, and the Flyweight pattern demands I dont 
        // crete a bunch of extra objects. also I dont wanna write new
        // everywhere.
        Signal onSignal = new Signal(Signal.ON);
        Signal offSignal = new Signal(Signal.OFF);
        Signal nextSpecialSignal = new Signal(Signal.NEXT_SPECIAL);
        Signal enterNightSignal = new Signal(Signal.ENTER_NIGHT);
        Signal exitNightSignal = new Signal(Signal.EXIT_NIGHT);
        Signal dimUpSignal = new Signal(Signal.DIM_UP);
        Signal dimDownSignal = new Signal(Signal.DIM_DOWN);
        Signal dimMinSignal = new Signal(Signal.DIM_MIN);
        Signal dimMaxSignal = new Signal(Signal.DIM_MAX);

        State offState = new State(State.OFF);
        State cctState = new State(State.CCT);
        State colorState = new State(State.COLOR);
        State nightOffState = new State(State.NIGHT_OFF);
        State nightOnState = new State(State.NIGHT_ON);
        State nightCCTState = new State(State.NIGHT_CCT);
        State nightColorState = new State(State.NIGHT_COLOR);


        //TODO fill out these state transitions
        // examples:
        // off
        stateMachine.setTransition(offState, onSignal, cctState, this::enterOFF);
        stateMachine.setTransition(offState, nextSpecialSignal, colorState, this::enterColor);

        // CCT
        stateMachine.setTransition(cctState, offSignal, offState, this::enterOFF);
    }
}
