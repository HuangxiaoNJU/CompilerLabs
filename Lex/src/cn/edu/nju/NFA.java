package cn.edu.nju;

import java.util.List;
import java.util.stream.Collectors;

public class NFA {

    private List<State> stateSet;
    private State startState;

    public NFA(State startState, List<State> stateSet) {
        this.startState = startState;
        this.stateSet = stateSet;
    }

    /**
     * 获取接受状态集
     */
    public List<State> getAcceptStates() {
        return stateSet
                .stream()
                .filter(State::isAcceptState)
                .collect(Collectors.toList());
    }

    /**
     * NFA . NFA' 合并
     */
    public void connectNFA(NFA nfa) {
        this.stateSet.addAll(nfa.stateSet);
        getAcceptStates().forEach(e -> {
            e.setAcceptState(false);
            e.addNextState('\0', nfa.getStartState());
        });
    }

    /**
     * NFA | NFA' 合并
     * @param newStart  新初始状态
     * @param newAccept 新接受状态
     */
    public void orNFA(NFA nfa, State newStart, State newAccept) {
        newStart.addNextState('\0', this.getStartState());
        newStart.addNextState('\0', nfa.getStartState());
        this.getAcceptStates().forEach(e -> {
            e.setAcceptState(false);
            e.addNextState('\0', newAccept);
        });
        nfa.getAcceptStates().forEach(e -> {
            e.setAcceptState(false);
            e.addNextState('\0', newAccept);
        });
        this.stateSet.addAll(nfa.stateSet);
        this.stateSet.add(newStart);
        this.stateSet.add(newAccept);
        this.startState = newStart;
    }

    /**
     * NFA* 闭包
     * @param newStart  新初始状态
     * @param newAccept 新接受状态
     */
    public void closureNFA(State newStart, State newAccept) {
        newStart.addNextState('\0', this.startState);
        newStart.addNextState('\0', newAccept);
        // 添加新状态
        this.stateSet.add(newStart);
        this.stateSet.add(newAccept);
        // 修改状态转移
        this.getAcceptStates().forEach(e -> {
            e.setAcceptState(false);
            e.addNextState('\0', newAccept);
            e.addNextState('\0', startState);
        });
        // 设置新初始状态
        this.startState = newStart;
    }

    public State getStartState() {
        return startState;
    }
}
