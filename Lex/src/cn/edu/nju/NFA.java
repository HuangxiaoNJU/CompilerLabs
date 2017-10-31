package cn.edu.nju;

import java.util.List;

public class NFA extends FA {

    private static final char EPSILON = '\0';

    public NFA(State startState, List<State> stateSet) {
        super(startState, stateSet);
    }

    /**
     * NFA . NFA' 合并
     */
    public void connectNFA(NFA nfa) {
        // 修改状态转移
        getAcceptStates().forEach(e -> {
            e.setAcceptState(false);
            e.addNextState(EPSILON, nfa.getStartState());
        });
        stateSet.addAll(nfa.stateSet);
    }

    /**
     * NFA | NFA' 合并
     * @param newStart  新初始状态
     * @param newAccept 新接受状态
     */
    public void orNFA(NFA nfa, State newStart, State newAccept) {
        newStart.addNextState(EPSILON, this.getStartState());
        newStart.addNextState(EPSILON, nfa.getStartState());
        this.getAcceptStates().forEach(e -> {
            e.setAcceptState(false);
            e.addNextState(EPSILON, newAccept);
        });
        nfa.getAcceptStates().forEach(e -> {
            e.setAcceptState(false);
            e.addNextState(EPSILON, newAccept);
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
        newStart.addNextState(EPSILON, this.startState);
        newStart.addNextState(EPSILON, newAccept);
        // 修改状态转移
        this.getAcceptStates().forEach(e -> {
            e.setAcceptState(false);
            e.addNextState(EPSILON, newAccept);
            e.addNextState(EPSILON, startState);
        });
        // 添加新状态
        this.stateSet.add(newStart);
        this.stateSet.add(newAccept);
        // 设置新初始状态
        this.startState = newStart;
    }


}
