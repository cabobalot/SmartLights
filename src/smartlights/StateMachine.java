package smartlights;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class StateMachine {
	
	// currentState<signal<newState, doNewState()>>>
	protected List<List<Pair<State, Consumer<Signal>>>> transitions;
	protected State currentState = new State(State.OFF);

	public StateMachine() {

		// create all transitions with empty functions and a return to off
		transitions = Collections.nCopies(7, Collections.nCopies(9, new Pair<>(new State(State.OFF), this::n)));
	}

	public void setTransition(State oldState, Signal signal, State newState, Consumer<Signal> newStateFunction) {
		transitions.get(oldState.state).set(signal.signal, new Pair<>(newState, newStateFunction));
	}

	public void transition(Signal s) {
		Pair<State, Consumer<Signal>> p = transitions.get(currentState.state).get(s.signal);
		currentState = p.first;

		p.second.accept(s);
	}

	/**
	 * no state change / do nothing
	 */
	protected void n(Signal s) {}

	// extendable enums :(

	public static class State {
		public int state;

		public State(int state) {
			this.state = state;
		}
		
		public static final int OFF = 0;
		public static final int CCT = 1;
		public static final int COLOR = 2;
		public static final int NIGHT_OFF = 3;
		public static final int NIGHT_ON = 4;
		public static final int NIGHT_CCT = 5;
		public static final int NIGHT_COLOR = 6;
		
	}

	public static class Signal {
		public int signal;

		public Signal(int signal) {
			this.signal = signal;
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
