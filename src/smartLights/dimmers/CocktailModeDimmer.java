package smartLights.dimmers;

import java.util.Set;

import smartLights.LightState;
import smartLights.RoomState;

public class CocktailModeDimmer implements Dimmer {
	private int currentIndex = 0;
	private Set<String> primaryNames;
	private Set<String> secondaryNames;

	private int hue = 0;
	private int cct = 500;

	public static final int[] secondaryBrightnesses = {0, 5, 50, 100}; 

	public CocktailModeDimmer(Set<String> primaryNames, Set<String> secondaryNames) {
		this.primaryNames = primaryNames;
		this.secondaryNames = secondaryNames;
	}

	public void setCCT(int cct) {
		this.cct = cct;
	}

	public void setHue(int hue) {
		this.hue = hue;
	}

	@Override
	public RoomState dimUp() {
		currentIndex++;
		if (currentIndex >= niceBrightnesses.length) {
			currentIndex = niceBrightnesses.length - 1; 
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
		currentIndex = niceBrightnesses.length - 1;
		return getRoomState();
	}

	@Override
	public RoomState dimMin() {
		currentIndex = 0;
		return getRoomState();
	}

	@Override
	public RoomState firstOn() {
		currentIndex = niceBrightnesses.length - 1; 
		return getRoomState();
	}

	/**
	 * Dynamically calculate the current lights according to the CCT
	 */
	@Override
	public RoomState getRoomState() {
		RoomState room = new RoomState();
		
		for (String name : primaryNames) {
			LightState state = new LightState(niceBrightnesses[currentIndex], cct);
			room.lightStates.put(name, state);
		}

		for (String name : secondaryNames) {
			LightState state = new LightState(secondaryBrightnesses[currentIndex], hue, 100);
			room.lightStates.put(name, state);
		}

		return room;
	}

	@Override
	public void setIndex(int index) {
		if (index > niceBrightnesses.length - 1) {
			currentIndex = niceBrightnesses.length - 1;
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
