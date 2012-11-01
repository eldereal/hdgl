package hdgl.db.query.stm;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import hdgl.db.query.condition.AbstractCondition;

public class StateMachine {
	
	public static enum TransitionType{
		In, Out, Backtrack, Success
	}
	
	public static class Transition{
		private TransitionType type;
		private AbstractCondition test;
		private int toState;
		
		public Transition(TransitionType type, AbstractCondition test,
				int toState) {
			super();
			this.type = type;
			this.test = test;
			this.toState = toState;
		}
		public TransitionType getType() {
			return type;
		}
		public void setType(TransitionType type) {
			this.type = type;
		}
		public AbstractCondition getTest() {
			return test;
		}
		public void setTest(AbstractCondition test) {
			this.test = test;
		}
		public int getToState() {
			return toState;
		}
		public void setToState(int toState) {
			this.toState = toState;
		}
	}
	
	public static class Condition{
		
		private AbstractCondition test;
		private ArrayList<Transition> transitions = new ArrayList<StateMachine.Transition>();
		
		public int getSize(){
			return transitions.size();
		}
		
		public AbstractCondition getTest() {
			return test;
		}
		
		public void setTest(AbstractCondition test) {
			this.test = test;
		}
		
		public Transition getTransition(int transId) {
			return transitions.get(transId);
		}
		
		Iterable<Transition> getTransitions(){
			return transitions;
		}
		
		public int addTransition(Transition transition) {
			this.transitions.add(transition);
			return transitions.size()-1;
		}
		
		public Condition(AbstractCondition test) {
			super();
			this.test = test;			
		}
	}
	
	public static class State{
		
		ArrayList<Condition> conditions = new ArrayList<Condition>();

		public int getSize(){
			return conditions.size();
		}
		
		public Condition getCondition(int condId){
			return conditions.get(condId);
		}
		
		public Iterable<Condition> getConditions(){
			return conditions;
		}

		public int addCondition(Condition cond) {
			this.conditions.add(cond);
			return conditions.size()-1;
		}
	}
	
	int maxState = 1;
	Map<Integer, State> states = new HashMap<Integer, StateMachine.State>();
	
	public int addState(){
		int stateId = maxState;
		maxState++;
		states.put(stateId, new State());
		return stateId;
	}
	
	public State getState(int stateId){
		return states.get(stateId);
	}
	
	public Iterable<State> getStates(){
		return states.values();
	}
	
	public void print(PrintStream out){
		out.println("states\tconditions\ttransitions");
		for (Map.Entry<Integer, State> states : this.states.entrySet()) {
			out.print(states.getKey()+"\t");
			int condsize=states.getValue().getSize();
			for(int i=0;i<condsize;i++){
				Condition cond = states.getValue().getCondition(i);
				if(i>0){
					out.print("\t");
				}
				out.print(cond.getTest()+"\t\t");
				for(int j=0;j<cond.getSize();j++){
					Transition transition = cond.getTransition(j);
					if(j>0){
						out.print("\t\t\t");
					}
					out.println(transition.type + "[" + transition.test + "] -> "+transition.toState);
				}
				
			}
		}
	}
}
