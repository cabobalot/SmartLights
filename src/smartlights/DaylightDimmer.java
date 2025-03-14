package smartlights;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DaylightDimmer implements Dimmer {
	private Set<String> lightNames;
	private List<Map<String, Integer>> brightnesses;
	private int currentIndex = 0;
	private int CCT = 300;

	/**
	 * create with the same brightness profile for every light.
	 * @param lightNames
	 */
	public DaylightDimmer(Set<String> lightNames) {
		this.lightNames = lightNames;

		brightnesses = new ArrayList<>();
		for (int i = 0; i < niceBrightnesses.length; i++) {
			Map<String, Integer> toAdd = new HashMap<>();
			for (String name : lightNames) {
				toAdd.put(name, niceBrightnesses[i]);
			}
			brightnesses.add(toAdd);
		}
	}

	/**
	 * create with custom profile
	 * @param lightNames
	 * @param brightnesses
	 */
	public DaylightDimmer(Set<String> lightNames, List<Map<String, Integer>> brightnesses) {
		this.lightNames = lightNames;
		this.brightnesses = brightnesses;
	}

	public void setCCT(int cct) {
		this.CCT = cct;
	}

	@Override
	public RoomState dimUp() {
		currentIndex++;
		if (currentIndex >= brightnesses.size()) {
			currentIndex = brightnesses.size() - 1; 
		}
		return getRoomState();
	}

	@Override
	public RoomState dimDown() {
		currentIndex--;
		if (currentIndex <= 0) {
			currentIndex = 0;
		}
		return getRoomState();
	}

	@Override
	public RoomState dimMax() {
		currentIndex = brightnesses.size() - 1;
		return getRoomState();
	}

	@Override
	public RoomState dimMin() {
		currentIndex = 0;
		return getRoomState();
	}

	@Override
	public RoomState firstOn() {
		currentIndex = brightnesses.size() - 1; 
		return getRoomState();
	}

	/**
	 * Dynamically calculate the current lights according to the CCT
	 */
	@Override
	public RoomState getRoomState() {
		RoomState state = new RoomState();

		for (String name : lightNames) {
			LightState light = new LightState(brightnesses.get(currentIndex).get(name), CCT);
			state.lightStates.put(name, light);
		}

		return state;
	}

	@Override
	public void setIndex(int index) {
		if (index > brightnesses.size() - 1) {
			currentIndex = brightnesses.size() - 1;
		}
		else if (index < 0) {
			currentIndex = 0;
		}
		else {
			currentIndex = index;
		}
	}

	@Override
	public int getCurrentIndex() {
		return currentIndex;
	}

}
