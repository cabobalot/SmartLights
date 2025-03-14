
public interface Dimmer {

	public static final int[] niceBrightnesses = {5, 20, 50, 90};

	public RoomState dimUp();

	public RoomState dimDown();

	public RoomState dimMax();

	public RoomState dimMin();

	/**
	 * Dim to the initial on state for this dimmer.
	 */ 
	public RoomState firstOn();

	public RoomState getRoomState();


}

