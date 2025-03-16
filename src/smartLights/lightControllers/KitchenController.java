package smartLights.lightControllers;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;

import smartLights.SmartRandom;
import smartLights.StateMachine.Signal;
import smartLights.StateMachine.State;
import smartLights.dimmers.CocktailModeDimmer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class KitchenController extends LightController {

    private Thread discoThread;
    private boolean discoRunning = false;

    private CocktailModeDimmer cocktailDimmer;
    private SmartRandom random = new SmartRandom(6, 3);
    private static int STATE_COCKTAIL = State.STATE_COUNT;

    public KitchenController(MqttAsyncClient client) {
        super(client, "zigbee2mqtt/KitchenSwitch/action");

        registerLightTopic("Kitchen1", "zigbee2mqtt/KitchenLight1/set");
        registerLightTopic("Kitchen2", "zigbee2mqtt/KitchenLight2/set");
        registerLightTopic("Kitchen3", "zigbee2mqtt/KitchenLight3/set");
        registerLightTopic("Kitchen4", "zigbee2mqtt/KitchenLight4/set");
        registerLightTopic("Kitchen5", "zigbee2mqtt/KitchenLight5/set");
        registerLightTopic("KitchenCabinet", "zigbee2mqtt/KitchenCabinet/set");

        generateDimmers();

        cocktailDimmer = new CocktailModeDimmer(Set.of("Kitchen4", "Kitchen5"), Set.of("Kitchen1", "Kitchen2", "Kitchen3", "KitchenCabinet"));

        State cocktailState = new State(STATE_COCKTAIL);
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
        stateMachine.addState(cocktailState);

        // to cocktail state
        stateMachine.setTransition(colorState, nextSpecialSignal, cocktailState, this::enterCocktailMode);

        stateMachine.setTransition(cocktailState, offSignal, offState, this::enterOFF);
        stateMachine.setTransition(cocktailState, enterNightSignal, nightOffState, this::enterNightOff);
        stateMachine.setTransition(cocktailState, nextSpecialSignal, cctState, this::enterCCT);
        // loopback dim signals
        stateMachine.setTransition(cocktailState, dimMaxSignal, cocktailState, this::dimMax);
        stateMachine.setTransition(cocktailState, dimMinSignal, cocktailState, this::dimMin);
        stateMachine.setTransition(cocktailState, dimUpSignal, cocktailState, this::dimUp);
        stateMachine.setTransition(cocktailState, dimDownSignal, cocktailState, this::dimDown);

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
        cocktailDimmer.setCCT(cct);
        super.setCCT(cct);
    }

    //disco...
    @Override
    public void downLeftLong() {
        if (discoRunning) {
            discoRunning = false;

            System.out.println("Disco mode turned off :(");
        }
        else {
            discoRunning = true;
            discoThread = new Thread(this::doTheDisco);
            discoThread.start();
            System.out.println("Disco mode on :)");
        }

    }

    private void enterCocktailMode(State state, Signal signal) {
        cocktailDimmer.setIndex(currentDimmer.getCurrentIndex());
        cocktailDimmer.setHue(random.getRandomInt() * 6);
        currentDimmer = cocktailDimmer;
        broadcastRoomState();
    }

    private void doTheDisco() {
        // TODO notify HouseStructure to set all controllers in disco mode
        Map<String, SmartRandom> randoms = new HashMap<>();
        allLightTopics.forEach((String name, String topic) -> {
            randoms.putIfAbsent(name, new SmartRandom(6, 3));
        });

        while (discoRunning) { // pick random colors every second for every light
            allLightTopics.forEach((String name, String topic) -> {
                String message = String.format("""
                    {"color":{"hue":%d,"saturation":%d}, "brightness":%d}""", randoms.getOrDefault(name, new SmartRandom(6, 3)).getRandomInt() * 60, 100, 80);
                broadcast(message, topic);
            });
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

        }
    }
}
