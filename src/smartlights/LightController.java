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

    protected StateMachine stateMachine = new StateMachine();

    protected Dimmer currentDimmer;
    protected DaylightDimmer daylightDimmer;
    protected ListDimmer colorDimmer;
    protected LightState offState = new LightState(0, 0); // object to send when the light needs to be off

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
        System.out.println("Dim up");
        stateMachine.transition(new Signal(Signal.DIM_UP));
    }

    public void dimDown() {
		System.out.println("Dim down");
        stateMachine.transition(new Signal(Signal.DIM_DOWN));
    }

    public void turnOn() {
        System.out.print("Light on ...");
        stateMachine.transition(new Signal(Signal.ON));
        System.out.println("done");
    }

    public void turnOff() {
        stateMachine.transition(new Signal(Signal.OFF));
    }

    public void setNightMode() {
        stateMachine.transition(new Signal(Signal.ENTER_NIGHT));
    }

    public void setDayMode() {
        stateMachine.transition(new Signal(Signal.EXIT_NIGHT));
    }

    // are these needed?
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
    // are these needed?
    public void stopColorMode() {
        // System.out.print("color off ...");
        // generalLightState.setMode(LightState.Mode.CCT);
        // broadcastAll(generalLightState.getFullString());
        // System.out.println("done");
    }

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
        broadcastAll(offState.getFullString());
        System.out.println("done");
	}

	protected void enterCCT(State oldState, Signal signal) {
        switch (signal.signal) {
            case Signal.DIM_DOWN:
                if (oldState.state == State.OFF) {
                    daylightDimmer.dimMin();
                }
                else {
                    daylightDimmer.dimDown(); 
                }
                break;
                
            case Signal.DIM_MIN:
                daylightDimmer.dimMin();
                break;

            case Signal.DIM_UP:
                if (oldState.state == State.OFF) {
                    daylightDimmer.dimMax();
                }
                else { 
                    daylightDimmer.dimUp(); 
                }
                break;

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
                System.out.println("entered CCT with a weird signal: " + signal.signal);
                break;
        }
        currentDimmer = daylightDimmer;
        broadcastRoomState();
	}

	protected void enterColor(State oldState, Signal signal) {
        switch (signal.signal) {
            case Signal.DIM_DOWN:
                colorDimmer.dimDown();
                break;

            case Signal.DIM_MIN:
                colorDimmer.dimMin();
                break;

            case Signal.DIM_UP:
                colorDimmer.dimUp();

            case Signal.DIM_MAX:
                colorDimmer.dimMax();
                break;

            case Signal.EXIT_NIGHT:
            case Signal.NEXT_SPECIAL:
                if (oldState.state == State.OFF) {
                    colorDimmer.firstOn();
                }
                else {
                    colorDimmer.setIndex(currentDimmer.getCurrentIndex());
                }
                break;

            default:
                System.out.println("WARNING: entered CCT with a weird signal: " + signal.signal);
                break;
        }
        currentDimmer = colorDimmer;
        broadcastRoomState();
	}

	protected void enterNightOff(State oldState, Signal signal) {
	}

	protected void enterNightOn(State oldState, Signal signal) {
	}

	protected void enterNightCCT(State oldState, Signal signal) {
	}

	protected void enterNightColor(State oldState, Signal signal) {
	}

    /**
     * Broadcast the daylight CCT if in CCT mode.
     */
    public void setCCT(int cct) {
        daylightDimmer.setCCT(cct);

        if (!stateMachine.isAnyOff()) {
            broadcastRoomState(); // will only change anything if the current dimmer is the daylight dimmer
        }
    }

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
     * helper to create the default dimmers. only do this
     * after registering the light topics.
     */
    protected void generateDimmers() {
        daylightDimmer = new DaylightDimmer(lightTopics.keySet());
        colorDimmer = new ListDimmer(lightTopics.keySet());

        currentDimmer = daylightDimmer;
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


        // off
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
        stateMachine.setTransition(cctState, dimMaxSignal, cctState, this::enterCCT);
        stateMachine.setTransition(cctState, dimMinSignal, cctState, this::enterCCT);
        stateMachine.setTransition(cctState, dimUpSignal, cctState, this::enterCCT);
        stateMachine.setTransition(cctState, dimDownSignal, cctState, this::enterCCT);

        // Color
        stateMachine.setTransition(colorState, offSignal, offState, this::enterOFF);
        stateMachine.setTransition(colorState, enterNightSignal, nightOffState, this::enterNightOff);
        stateMachine.setTransition(colorState, nextSpecialSignal, cctState, this::enterCCT);
        // loopback dim signals
        stateMachine.setTransition(colorState, dimMaxSignal, colorState, this::enterColor);
        stateMachine.setTransition(colorState, dimMinSignal, colorState, this::enterColor);
        stateMachine.setTransition(colorState, dimUpSignal, colorState, this::enterColor);
        stateMachine.setTransition(colorState, dimDownSignal, colorState, this::enterColor);




    }
}
