package smartLights.dimmers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import smartLights.LightState;
import smartLights.RoomState;
import smartLights.SmartRandom;

public class ColorDimmer extends ListDimmer {
	private Set<String> lightNames;
	private List<Map<String, Integer>> brightnesses;
	private SmartRandom random = new SmartRandom(6, 3);

	public static final int[] niceSaturations = {100, 90, 80, 70};

	/**
	 * create with the same brightness profile for every light.
	 * @param lightNames
	 */
	public ColorDimmer(Set<String> lightNames) {
		this.lightNames = lightNames;

		brightnesses = new ArrayList<>();
		for (int i = 0; i < niceBrightnesses.length; i++) {
			Map<String, Integer> toAdd = new HashMap<>();
			for (String name : lightNames) {
				toAdd.put(name, niceBrightnesses[i]);
			}
			brightnesses.add(toAdd);
		}

		generateNewScheme();
	}

	/**
	 * create with custom profile
	 * @param lightNames
	 * @param brightnesses
	 */
	public ColorDimmer(Set<String> lightNames, List<Map<String, Integer>> brightnesses) {
		this.lightNames = lightNames;
		this.brightnesses = brightnesses;

		generateNewScheme();
	}

	//TODO pull from a list of pre-made JSON state lists
	public void generateNewScheme() {
		int hue = random.getRandomInt() * 60;

		roomStates = new ArrayList<>();
		for (int i = 0; i < brightnesses.size(); i++) {
			RoomState room = new RoomState();
			
			for (String name : lightNames) {
				LightState state = new LightState(brightnesses.get(i).get(name), hue, niceSaturations[i]);
				room.lightStates.put(name, state);
			}
			roomStates.add(room);
		}
	}

	@Override
	public RoomState firstOn() {
		currentIndex = 1;
		return getRoomState();
	}


}
