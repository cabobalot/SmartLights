package smartlights;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

public class StateMachine {
	
	// currentState<signal<newState, doNewState(oldState, signal)>>>
	protected List<List<Pair<State, BiConsumer<State, Signal>>>> transitions;
	protected State currentState = new State(State.OFF);

	public StateMachine() {
		// create all transitions with empty functions and a return to off

		// this is immutable :(
		// transitions = Collections.nCopies(7, Collections.nCopies(9, new Pair<>(new State(State.OFF), this::n)));

		Pair<State, BiConsumer<State, Signal>> p = new Pair<>(new State(State.OFF), this::n); // default transition
		transitions = new ArrayList<>();
		for (int i = 0; i < State.SIGNAL_COUNT; i++) {
			List<Pair<State, BiConsumer<State, Signal>>> listToAdd = new ArrayList<>();
			for (int j = 0; j < Signal.SIGNAL_COUNT; j++) {
				listToAdd.add(p);
			}
			transitions.add(listToAdd);
		}
	}

	public void setTransition(State oldState, Signal signal, State newState, BiConsumer<State, Signal> newStateFunction) {
		transitions.get(oldState.state).set(signal.signal, new Pair<>(newState, newStateFunction));
	}

	public void transition(Signal s) {
		Pair<State, BiConsumer<State, Signal>> p = transitions.get(currentState.state).get(s.signal);
		State oldState = currentState;
		currentState = p.first;

		p.second.accept(oldState, s);
	}

	/**
	 * returns whether the state machine is in any of the off states.
	 * eg. off or night off 
	 */
	public boolean isAnyOff() {
		return (currentState.state == State.OFF) || currentState.state == State.NIGHT_OFF;
	}

	/**
	 * returns whether the state machine is in any of the on states.
	 * eg. *not* isAnyOff()
	 * @see isAnyOff()
	 */
	public boolean isAnyOn() {
		return !isAnyOff();
	}

	/**
	 * no state change / do nothing
	 */
	public void n(State state, Signal signal) {}

	// extendable enums :(

	public static class State {
		public int state;

		public State(int state) {
			this.state = state;
		}
		
		@Override
		public int hashCode() {
			return state;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			State other = (State) obj;
			if (state != other.state)
				return false;
			return true;
		}

		public static final int OFF = 0;
		public static final int CCT = 1;
		public static final int COLOR = 2;
		public static final int NIGHT_OFF = 3;
		public static final int NIGHT_ON = 4;
		public static final int NIGHT_CCT = 5;
		public static final int NIGHT_COLOR = 6;
		
		public static final int SIGNAL_COUNT = 7;
	}

	public static class Signal {
		public int signal;

		public Signal(int signal) {
			this.signal = signal;
		}

		@Override
		public int hashCode() {
			return signal;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Signal other = (Signal) obj;
			if (signal != other.signal)
				return false;
			return true;
		}

		public static final int ON = 0;
		public static final int OFF = 1;
		public static final int NEXT_SPECIAL = 2;
		public static final int ENTER_NIGHT = 3;
		public static final int EXIT_NIGHT = 4;
		public static final int DIM_UP = 5;
		public static final int DIM_DOWN = 6;
		public static final int DIM_MIN = 7;
		public static final int DIM_MAX = 8;

		public static final int SIGNAL_COUNT = 9;
	}

	public class Pair<A, B> {
		public final A first;
		public final B second;
	
		public Pair(A first, B second) {
			this.first = first;
			this.second = second;
		}
	}
}
