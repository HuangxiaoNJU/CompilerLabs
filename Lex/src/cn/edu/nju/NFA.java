package cn.edu.nju;

import java.util.List;

public class NFA extends FA {

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
            e.addNextState('\0', nfa.getStartState());
        });
        stateSet.addAll(nfa.stateSet);
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
        // 修改状态转移
        this.getAcceptStates().forEach(e -> {
            e.setAcceptState(false);
            e.addNextState('\0', newAccept);
            e.addNextState('\0', startState);
        });
        // 添加新状态
        this.stateSet.add(newStart);
        this.stateSet.add(newAccept);
        // 设置新初始状态
        this.startState = newStart;
    }


}
