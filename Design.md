**Ideas**

* light state is a data class to hold a single state, including colors, 
brightness etc
  * different brightness levels should be represented with a new state 
  object - maybe state objects are constant
  * a state may be applied to a single bulb or to a group of bulbs somehow
* maybe states can be grouped with bulb assignment in a way to set a
  complicated scene state of a whole room. Is a number a good enough way to
  label an individual bulb? ordered lists?
* need some way to order and store separate states for each brightness 
level of a room
  * somehow mix with modes. 
  maybe each mode has its own list of states for each brightness
  * a "dimmer" (come up with a better name) is an interface that represents
  a list of different states for different dimmer settings. the abstraction
  means it can store whole state objects, or calculate them on the fly, etc.
  * CCTDimmer, NightModeDimmer, ColorModeDimmer
  * changing modes is the same as changing which dimmer has control over a
  given room.
  * room is the key word here... how can a "room" be represented?
  * A room has a list of RoomStates.
  * a RoomState is a list of LightStates, one for each light
  * a RoomState can be applied to the room, it sends each state to 
  each light in its tree

**New Structure**

* light state is a data class to hold a single state, including colors,
  brightness etc
  * different brightness levels should be represented with a new state
    object - maybe state objects are constant
  * a state may be applied to a single bulb or to a group of bulbs through the dimmer
* a RoomState is a list of LightStates, one for each light
  * LightStates must be tied to an individual light
* A RoomDimmer switches through RoomStates depending on the dimmer setting.
  * abstract interface
* LightController has the large state machine to change dimmers and turn on/off