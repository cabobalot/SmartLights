package smartLights.lightControllers;
import java.util.Set;

import org.eclipse.paho.client.mqttv3.MqttAsyncClient;

import smartLights.HouseStructure;
import smartLights.SmartRandom;
import smartLights.StateMachine.Signal;
import smartLights.StateMachine.State;
import smartLights.dimmers.CocktailModeDimmer;


public class LoungeController extends LightController {

    private CocktailModeDimmer movieDimmer;
    private SmartRandom random = new SmartRandom(6, 3);
    private static int STATE_MOVIE = State.STATE_COUNT;

    //mosquitto_pub -h 192.168.1.20 -t "zigbee2mqtt/LoungeSwitch/action" -m "1_single"
    public LoungeController(MqttAsyncClient client) {
        super(client, "zigbee2mqtt/LoungeSwitch/action");

        registerLightTopic("Lounge1", "zigbee2mqtt/LoungeLight1/set");
        registerLightTopic("Lounge2", "zigbee2mqtt/LoungeLight2/set");
        registerLightTopic("Lounge3", "zigbee2mqtt/LoungeLight3/set");

        generateDimmers();
        
        movieDimmer = new CocktailModeDimmer(Set.of("Lounge3"), Set.of("Lounge1", "Lounge2"));
        movieDimmer.setMySecondaryBrightnesses(new int[] {0, 0, 0, 0});

        State movieState = new State(STATE_MOVIE);
        Signal offSignal = new Signal(Signal.OFF);
        Signal nextSpecialSignal = new Signal(Signal.NEXT_SPECIAL);
        Signal enterNightSignal = new Signal(Signal.ENTER_NIGHT);
        Signal dimUpSignal = new Signal(Signal.DIM_UP);
        Signal dimDownSignal = new Signal(Signal.DIM_DOWN);
        Signal dimMinSignal = new Signal(Signal.DIM_MIN);
        Signal dimMaxSignal = new Signal(Signal.DIM_MAX);

        State offState = new State(State.OFF);
        State cctState = new State(State.CCT);
        State colorState = new State(State.COLOR);
        State nightOffState = new State(State.NIGHT_OFF);
        stateMachine.addState(movieState);

        // to cocktail state
        stateMachine.setTransition(colorState, nextSpecialSignal, movieState, this::enterMovieMode);

        stateMachine.setTransition(movieState, offSignal, offState, this::enterOFF);
        stateMachine.setTransition(movieState, enterNightSignal, nightOffState, this::enterNightOff);
        stateMachine.setTransition(movieState, nextSpecialSignal, cctState, this::enterCCT);
        // loopback dim signals
        stateMachine.setTransition(movieState, dimMaxSignal, movieState, this::dimMax);
        stateMachine.setTransition(movieState, dimMinSignal, movieState, this::dimMin);
        stateMachine.setTransition(movieState, dimUpSignal, movieState, this::dimUp);
        stateMachine.setTransition(movieState, dimDownSignal, movieState, this::dimDown);

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
    public void setCCT(int cct) {
        movieDimmer.setCCT(cct);
        super.setCCT(cct);
    }

    public void enterMovieMode (State state, Signal signal) {
        movieDimmer.setIndex(currentDimmer.getCurrentIndex());
        movieDimmer.setHue(random.getRandomInt() * 60);
        currentDimmer = movieDimmer;
        broadcastRoomState();
    }

    @Override
    public void upLeftLong() {
        HouseStructure.wholeHouseOff();
    }
}
