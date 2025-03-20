package smartLights.dimmers;

import java.util.Set;

import smartLights.LightState;
import smartLights.RoomState;

public class CocktailModeDimmer implements Dimmer {
	private int currentIndex = 0;
	private Set<String> primaryNames;
	private Set<String> secondaryNames;

	private int[] myPrimaryBrightnesses;
	private int[] mySecondaryBrightnesses;

	private int hue = 0;
	private int cct = 500;

	public static final int[] niceSecondaryBrightnesses = {5, 10, 50, 100};

	public CocktailModeDimmer(Set<String> primaryNames, Set<String> secondaryNames) {
		this.primaryNames = primaryNames;
		this.secondaryNames = secondaryNames;

		myPrimaryBrightnesses = niceBrightnesses;
		mySecondaryBrightnesses = niceSecondaryBrightnesses;
	}
	
	public void setMyPrimaryBrightnesses(int[] myPrimaryBrightnesses) {
		this.myPrimaryBrightnesses = myPrimaryBrightnesses;
	}

	public void setMySecondaryBrightnesses(int[] mySecondaryBrightnesses) {
		this.mySecondaryBrightnesses = mySecondaryBrightnesses;
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
		if (currentIndex >= myPrimaryBrightnesses.length) {
			currentIndex = myPrimaryBrightnesses.length - 1; 
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
		currentIndex = myPrimaryBrightnesses.length - 1;
		return getRoomState();
	}

	@Override
	public RoomState dimMin() {
		currentIndex = 0;
		return getRoomState();
	}

	@Override
	public RoomState firstOn() {
		currentIndex = myPrimaryBrightnesses.length - 1; 
		return getRoomState();
	}

	/**
	 * Dynamically calculate the current lights according to the CCT
	 */
	@Override
	public RoomState getRoomState() {
		RoomState room = new RoomState();
		
		for (String name : primaryNames) {
			LightState state = new LightState(myPrimaryBrightnesses[currentIndex], cct);
			room.lightStates.put(name, state);
		}

		for (String name : secondaryNames) {
			LightState state = new LightState(mySecondaryBrightnesses[currentIndex], hue, 100);
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
