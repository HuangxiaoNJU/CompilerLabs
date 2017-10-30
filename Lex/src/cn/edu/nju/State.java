package cn.edu.nju;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class State {

    // 状态id
    private int stateId;
    private Map<Character, Set<State>> nextState;
    private boolean isAcceptState;

    public State(int stateId) {
        this.stateId = stateId;
        this.nextState = new HashMap<>();
        this.isAcceptState = false;
    }

    public State(int stateId, boolean isAcceptState) {
        this.stateId = stateId;
        this.nextState = new HashMap<>();
        this.isAcceptState = isAcceptState;
    }

    // 添加后继状态
    public void addNextState(char c, State state) {
        if (nextState.containsKey(c)) {
            nextState.get(c).add(state);
        } else {
            Set<State> set = new HashSet<>();
            set.add(state);
            nextState.put(c, set);
        }
    }

    /**
     * 获取通过c边到达的状态集
     */
    public Set<State> next(char c) {
        if (nextState.containsKey(c)) {
            return nextState.get(c);
        }
        return new HashSet<>();
    }

    /**
     * 获取状态所有出边字符集合
     */
    public Set<Character> getEdgeSet() {
        return nextState.keySet();
    }

    public int getStateId() {
        return stateId;
    }

    public void setNextState(Map<Character, Set<State>> nextState) {
        this.nextState = nextState;
    }

    public boolean isAcceptState() {
        return isAcceptState;
    }

    public void setAcceptState(boolean acceptState) {
        isAcceptState = acceptState;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Character character : nextState.keySet()) {
            for (State state : nextState.get(character)) {
                sb.append(stateId).append(" --").append(character).append("--> ").append(state.stateId).append('\n');
            }
        }
        return sb.toString();
    }
}
