package smartlights;
import java.util.HashMap;
import java.util.Map;

public class RoomState {
	public Map<String, LightState> lightStates;

	public RoomState() {
		this.lightStates = new HashMap<>();
	}

	public RoomState(HashMap<String, LightState> lightStates) {
		this.lightStates = lightStates;
	}
}
