package smartLights.dimmers;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import smartLights.LightState;
import smartLights.RoomState;

// Maybe this class shouldn't exist, and instead we should create more specific dimmers...
// Or maybe this class can provide a way to modify the list, (placeholders maybe?)
// or maybe I suck it up and make a ListDimmerFactory...
public class ListDimmer implements Dimmer {
	protected List<RoomState> roomStates;
	protected int currentIndex = 0;

	/**
	 * create a generic dimming list in magenta
	 * @param lightNames
	 */
	public ListDimmer(Set<String> lightNames) {
		roomStates = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			RoomState room = new RoomState();
			// create one state object and duplicate the pointer for every light
			LightState state = new LightState(niceBrightnesses[i], 300, 100);
			for (String name : lightNames) {
				room.lightStates.put(name, state);
			}
			roomStates.add(room);
		}
	}

	public ListDimmer(List<RoomState> roomStates) {
		this.roomStates = roomStates;
	}

	/**
	 * Generic constructor that creates an empty roomStates list.
	 * roomStates must be populated manually after this
	 */
	protected ListDimmer() {
		roomStates = new ArrayList<>();
	}

	public RoomState dimUp() {
		currentIndex++;
		if (currentIndex >= roomStates.size()) {
			currentIndex = roomStates.size() - 1; 
		}
		return getRoomState();
	}

	public RoomState dimDown() {
		currentIndex--;
		if (currentIndex <= 0) {
			currentIndex = 0;
		}
		return getRoomState();
	}

	public RoomState dimMax() {
		currentIndex = roomStates.size() - 1;
		return getRoomState();
	}

	public RoomState dimMin() {
		currentIndex = 0;
		return getRoomState();
	}

	public RoomState getRoomState() {
		return roomStates.get(currentIndex);
	}

	/**
	 * {@inheritDoc} <p>
	 * This implementation starts on max brightness
	 */
	public RoomState firstOn() {
		currentIndex = roomStates.size() - 1;
		return getRoomState();
	}

	@Override
	public int getCurrentIndex() {
		return currentIndex;
	}

	@Override
	public void setIndex(int index) {
		if (index > roomStates.size() - 1) {
			currentIndex = roomStates.size() - 1;
		}
		else if (index < 0) {
			currentIndex = 0;
		}
		else {
			currentIndex = index;
		}
	}
}

