package smartLights.dimmers;

import smartLights.RoomState;

public interface Dimmer {

	public static final int[] niceBrightnesses = {5, 20, 100, 230};

	public RoomState dimUp();

	public RoomState dimDown();

	public RoomState dimMax();

	public RoomState dimMin();

	/**
	 * Dim to the initial on state for this dimmer.
	 */ 
	public RoomState firstOn();

	public RoomState getRoomState();

	/**
	 * should only be used when changing which dimmer is in control
	 * @see getCurrentIndex
	 * @param index
	 */
	public void setIndex(int index);

	public int getCurrentIndex();
}

