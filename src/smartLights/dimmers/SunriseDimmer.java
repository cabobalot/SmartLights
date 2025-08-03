package smartLights.dimmers;

import java.util.List;

import smartLights.HouseStructure;
import smartLights.LightState;
import smartLights.RoomState;

public class SunriseDimmer implements Dimmer {

	private List<String> lightNames;
	private int minute = 0;
	private int offset;

	public SunriseDimmer(List<String> lightNames, int offset) {
		this.lightNames = lightNames;
		this.offset = offset;
	}

	public void setMinute(int min) {
		minute = min;
	}

	@Override
	public RoomState dimUp() {
		return getRoomState();
	}

	@Override
	public RoomState dimDown() {
		return getRoomState();
	}

	@Override
	public RoomState dimMax() {
		return getRoomState();
	}

	@Override
	public RoomState dimMin() {
		return getRoomState();
	}

	@Override
	public RoomState firstOn() {
		return getRoomState();
	}

	@Override
	public RoomState getRoomState() {
		RoomState room = new RoomState();
		for (int i = 0; i < lightNames.size(); i++) {
			room.lightStates.put(lightNames.get(i), HouseStructure.getNthSunriseState(offset + i, minute));
		}
		return room;
	}

	@Override
	public void setIndex(int index) {
		
	}

	@Override
	public int getCurrentIndex() {
		return 2;
	}

}
