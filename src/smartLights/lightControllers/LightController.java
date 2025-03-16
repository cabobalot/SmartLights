package smartLights.lightControllers;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import smartLights.LightState;
import smartLights.Main;
import smartLights.RoomState;
import smartLights.SmartRandom;
import smartLights.StateMachine;
import smartLights.StateMachine.Signal;
import smartLights.StateMachine.State;
import smartLights.dimmers.ColorDimmer;
import smartLights.dimmers.DaylightDimmer;
import smartLights.dimmers.Dimmer;
import smartLights.dimmers.ListDimmer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public abstract class LightController implements IMqttMessageListener {
    protected HashMap<String, String> lightTopics = new HashMap<>();
    protected static HashMap<String, String> allLightTopics = new HashMap<>();
    protected static HashMap<String, String> allRelayTopics = new HashMap<>();
    protected MqttAsyncClient client;

    protected StateMachine stateMachine = new StateMachine();

    protected Dimmer currentDimmer;
    protected DaylightDimmer daylightDimmer;
    protected ColorDimmer colorDimmer;
    protected ListDimmer nightDimmer;
    // protected LightState offState = new LightState(0, 0); // object to send when the light needs to be off
    protected String offStateMessage = "OFF"; // string to send when the light needs to be off

    protected SmartRandom random = new SmartRandom(6, 3);

    /**
     * Child classes should instantiate the dimmers
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

    /*
     * these are called on button presses
     */

     public void upLeftSingle() {
        if (stateMachine.isAnyOn()) {
            turnOff();
        }
        else {
            turnOn();
        }
    }

    public void upRightSingle() {
        dimUp();
    }

    public void downLeftSingle() {

    }

    public void downRightSingle() {
        dimDown();
    }

    public void upLeftDouble() {
        stateMachine.transition(new Signal(Signal.NEXT_SPECIAL));
    }

    public void upRightDouble() {
        stateMachine.transition(new Signal(Signal.DIM_MAX));
    }

    public void downLeftDouble() {

    }

    public void downRightDouble() {
        stateMachine.transition(new Signal(Signal.DIM_MIN));
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
     * send the dim up signal to the state machine
     */
    public void dimUp() {
        System.out.println("Dim up");
        stateMachine.transition(new Signal(Signal.DIM_UP));
    }

    /**
     * send the dim down signal to the state machine
     */
    public void dimDown() {
		System.out.println("Dim down");
        stateMachine.transition(new Signal(Signal.DIM_DOWN));
    }

    /**
     * send the on signal to the state machine
     */
    public void turnOn() {
        System.out.print("Light on ...");
        stateMachine.transition(new Signal(Signal.ON));
        System.out.println("done");
    }

    /**
     * send the off signal to the state machine
     */
    public void turnOff() {
        stateMachine.transition(new Signal(Signal.OFF));
    }

    /**
     * send the enter night signal to the state machine
     */
    public void setNightMode() {
        stateMachine.transition(new Signal(Signal.ENTER_NIGHT));
    }

    /**
     * send the exit night signal to the state machine
     */
    public void setDayMode() {
        stateMachine.transition(new Signal(Signal.EXIT_NIGHT));
    }

    /**
     * Broadcast the daylight CCT if in CCT mode.
     */
    public void setCCT(int cct) {
        daylightDimmer.setCCT(cct);

        if (stateMachine.isAnyOn()) {
            broadcastRoomState(); // will only change anything if the current dimmer is the daylight dimmer
        }
    }

    /*
     * broadcast mqtt messages
     */

    /**
     * broadcast the mqtt message for the current state in the current dimmer
     */
    protected void broadcastRoomState() {
        broadcastRoomState(currentDimmer.getRoomState());
    }

    /**
     * broadcast the mqtt message for the roomState
     */
    protected void broadcastRoomState(RoomState room) {
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

    protected void broadcastAll(String command) {
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

    protected void broadcast(String command, String topic) {
        MqttMessage message = new MqttMessage(command.getBytes());
        message.setQos(Main.qos);
        try {
            client.publish(topic, message);
        } catch (MqttException e) {
            Main.printError(e);
        }
    }

    /*
     * Below are the state machine functions.
     * These happen on entering a state due to a signal s. Do not call them directly.
     * They should usually switch the dimmer and/or broadcast the mqtt messages.
     */

    protected void enterOFF(State oldState, Signal s) {
        System.out.print("Light off ...");
        broadcastAll(offStateMessage);
        System.out.println("done");
	}

	protected void enterCCT(State oldState, Signal signal) {
        switch (signal.signal) {
            case Signal.DIM_DOWN:
            case Signal.DIM_MIN:
                daylightDimmer.dimMin();
                break;

            case Signal.DIM_UP:
            case Signal.DIM_MAX:
                daylightDimmer.dimMax();
                break;

            case Signal.ON:
                daylightDimmer.firstOn();
                break;

            case Signal.EXIT_NIGHT:
            case Signal.NEXT_SPECIAL:
                daylightDimmer.setIndex(currentDimmer.getCurrentIndex());
                break;

            default:
                System.out.println("WARNING: entered CCT state with a weird signal: " + signal.signal);
                break;
        }
        currentDimmer = daylightDimmer;
        broadcastRoomState();
	}

	protected void enterColor(State oldState, Signal signal) {
        switch (signal.signal) {
            case Signal.EXIT_NIGHT:
                colorDimmer.setIndex(currentDimmer.getCurrentIndex());
                break;
            case Signal.NEXT_SPECIAL:
                if (oldState.state == State.OFF) {
                    colorDimmer.firstOn();
                }
                else {
                    colorDimmer.setIndex(currentDimmer.getCurrentIndex());
                }
                break;

            default:
                System.out.println("WARNING: entered Color state with a weird signal: " + signal.signal);
                break;
        }
        colorDimmer.generateNewScheme();
        currentDimmer = colorDimmer;
        broadcastRoomState();
	}

	protected void enterNightOff(State oldState, Signal signal) {
        // System.out.print("Night off ...");
        broadcastAll(offStateMessage);
        // System.out.println("done");
	}

	protected void enterNightOn(State oldState, Signal signal) {
        switch (signal.signal) {
            case Signal.NEXT_SPECIAL:
                nightDimmer.setIndex(currentDimmer.getCurrentIndex());
                break;
            default:
                nightDimmer.firstOn();
                break;
        }
        currentDimmer = nightDimmer;
        broadcastRoomState();
	}

	protected void enterNightCCT(State oldState, Signal signal) {
        daylightDimmer.setIndex(currentDimmer.getCurrentIndex());
        currentDimmer = daylightDimmer;
        broadcastRoomState();
	}

	protected void enterNightColor(State oldState, Signal signal) {
        colorDimmer.setIndex(currentDimmer.getCurrentIndex());
        colorDimmer.generateNewScheme();
        currentDimmer = colorDimmer;
        broadcastRoomState();
	}

    /**
     * just dim the current dimmer
     */
    protected void dimUp(State oldState, Signal signal) {
        broadcastRoomState(currentDimmer.dimUp());
    }

    /**
     * just dim the current dimmer
     */
    protected void dimDown(State oldState, Signal signal) {
        broadcastRoomState(currentDimmer.dimDown());
    }

    /**
     * just dim the current dimmer
     */
    protected void dimMax(State oldState, Signal signal) {
        broadcastRoomState(currentDimmer.dimMax());
    }

    /**
     * just dim the current dimmer
     */
    protected void dimMin(State oldState, Signal signal) {
        broadcastRoomState(currentDimmer.dimMin());
    }

    protected void fillStateMachine() {
        // im so sorry for this. type safety requires using the Signal 
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


        // all the state transitions:

        // OFF
        stateMachine.setTransition(offState, onSignal, cctState, this::enterCCT);
        stateMachine.setTransition(offState, nextSpecialSignal, colorState, this::enterColor);
        stateMachine.setTransition(offState, enterNightSignal, nightOffState, this::enterNightOff);
        stateMachine.setTransition(offState, dimMaxSignal, cctState, this::enterCCT);
        stateMachine.setTransition(offState, dimMinSignal, cctState, this::enterCCT);
        stateMachine.setTransition(offState, dimUpSignal, cctState, this::enterCCT);
        stateMachine.setTransition(offState, dimDownSignal, cctState, this::enterCCT);

        // CCT
        stateMachine.setTransition(cctState, offSignal, offState, this::enterOFF);
        stateMachine.setTransition(cctState, enterNightSignal, nightOffState, this::enterNightOff);
        stateMachine.setTransition(cctState, nextSpecialSignal, colorState, this::enterColor);
        // loopback dim signals
        stateMachine.setTransition(cctState, dimMaxSignal, cctState, this::dimMax);
        stateMachine.setTransition(cctState, dimMinSignal, cctState, this::dimMin);
        stateMachine.setTransition(cctState, dimUpSignal, cctState, this::dimUp);
        stateMachine.setTransition(cctState, dimDownSignal, cctState, this::dimDown);

        // COLOR
        stateMachine.setTransition(colorState, offSignal, offState, this::enterOFF);
        stateMachine.setTransition(colorState, enterNightSignal, nightOffState, this::enterNightOff);
        stateMachine.setTransition(colorState, nextSpecialSignal, cctState, this::enterCCT);
        // loopback dim signals
        stateMachine.setTransition(colorState, dimMaxSignal, colorState, this::dimMax);
        stateMachine.setTransition(colorState, dimMinSignal, colorState, this::dimMin);
        stateMachine.setTransition(colorState, dimUpSignal, colorState, this::dimUp);
        stateMachine.setTransition(colorState, dimDownSignal, colorState, this::dimDown);

        // NIGHT_OFF
        stateMachine.setTransition(nightOffState, onSignal, nightOnState, this::enterNightOn);
        stateMachine.setTransition(nightOffState, exitNightSignal, offState, this::enterOFF);
        stateMachine.setTransition(nightOffState, dimMaxSignal, nightOnState, this::enterNightOn);
        stateMachine.setTransition(nightOffState, dimMinSignal, nightOnState, this::enterNightOn);
        stateMachine.setTransition(nightOffState, dimUpSignal, nightOnState, this::enterNightOn);
        stateMachine.setTransition(nightOffState, dimDownSignal, nightOnState, this::enterNightOn);

		// NIGHT_ON
        stateMachine.setTransition(nightOnState, offSignal, nightOffState, this::enterNightOff);
        stateMachine.setTransition(nightOnState, exitNightSignal, cctState, this::enterCCT);
        stateMachine.setTransition(nightOnState, nextSpecialSignal, nightColorState, this::enterNightColor);
        // loopback dim signals
        stateMachine.setTransition(nightOnState, dimMaxSignal, nightOnState, this::dimMax);
        stateMachine.setTransition(nightOnState, dimMinSignal, nightOnState, this::dimMin);
        stateMachine.setTransition(nightOnState, dimUpSignal, nightOnState, this::dimUp);
        stateMachine.setTransition(nightOnState, dimDownSignal, nightOnState, this::dimDown);

		// NIGHT_CCT
        stateMachine.setTransition(nightCCTState, offSignal, nightOffState, this::enterNightOff);
        stateMachine.setTransition(nightCCTState, nextSpecialSignal, nightOnState, this::enterNightOn);
        stateMachine.setTransition(nightCCTState, exitNightSignal, cctState, this::enterCCT);
        // loopback dim signals
        stateMachine.setTransition(nightCCTState, dimMaxSignal, nightCCTState, this::dimMax);
        stateMachine.setTransition(nightCCTState, dimMinSignal, nightCCTState, this::dimMin);
        stateMachine.setTransition(nightCCTState, dimUpSignal, nightCCTState, this::dimUp);
        stateMachine.setTransition(nightCCTState, dimDownSignal, nightCCTState, this::dimDown);

		// NIGHT_COLOR
        stateMachine.setTransition(nightColorState, offSignal, nightOffState, this::enterNightOff);
        stateMachine.setTransition(nightColorState, nextSpecialSignal, nightCCTState, this::enterNightCCT);
        stateMachine.setTransition(nightColorState, exitNightSignal, colorState, this::enterColor);
        // loopback dim signals
        stateMachine.setTransition(nightColorState, dimMaxSignal, nightColorState, this::dimMax);
        stateMachine.setTransition(nightColorState, dimMinSignal, nightColorState, this::dimMin);
        stateMachine.setTransition(nightColorState, dimUpSignal, nightColorState, this::dimUp);
        stateMachine.setTransition(nightColorState, dimDownSignal, nightColorState, this::dimDown);
    }

    /**
     * helper to create the default dimmers. only do this
     * after registering the light topics.
     */
    protected void generateDimmers() {
        Set<String> lightNames = lightTopics.keySet();
        daylightDimmer = new DaylightDimmer(lightNames);
        colorDimmer = new ColorDimmer(lightNames);

        // create a list of red color room states
        List<RoomState> roomStates = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			RoomState room = new RoomState();
			// create one state object and duplicate the pointer for every light
			LightState state = new LightState(Dimmer.niceBrightnesses[i], 0, 100);
			for (String name : lightNames) {
				room.lightStates.put(name, state);
			}
			roomStates.add(room);
		}
        nightDimmer = new ListDimmer(roomStates, 0);

        currentDimmer = daylightDimmer;
    }

}
