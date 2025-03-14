package smartlights;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ListDimmer implements Dimmer {
	protected List<RoomState> roomStates;
	protected int currentIndex = 0;

	/**
	 * create a generic dimming list
	 * @param lightNames
	 */
	public ListDimmer(Set<String> lightNames) {
		roomStates = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			RoomState room = new RoomState();
			// create one state object and duplicate the pointer for every light
			LightState state = new LightState(niceBrightnesses[i], 300);
			for (String name : lightNames) {
				room.lightStates.put(name, state);
			}
			roomStates.add(room);
		}
	}

	public ListDimmer(List<RoomState> roomStates) {
		this.roomStates = roomStates;
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

	public RoomState getRoomState() { // TODO this should handle daylight color temp (make a new class DaylightDimmer for that actually)
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

