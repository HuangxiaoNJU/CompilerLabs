package cn.edu.nju;

import java.util.*;

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

    private Set<State> reachable(Set<State> states, char c) {
        Set<State> res = new HashSet<>();
        states.forEach(e -> res.addAll(e.next(c)));
        return res;
    }

    /**
     * 深搜求epsilon闭包
     */
    private void dfs(Set<State> res, Set<State> toFind) {
        for (State state : toFind) {
            if (res.contains(state)) {
                return;
            }
            res.add(state);
            dfs(res, state.next(EPSILON));
        }
    }

    /**
     * 获取状态集epsilon闭包
     */
    private Set<State> epsilonClosure(Set<State> states) {
        Set<State> res = new HashSet<>();
        dfs(res, states);
        return res;
    }

    /**
     * 查看新子集是否在已有子集列表中
     * @param subsetList    子集列表
     * @param subset        新子集
     * @return              新子集在子集列表中下标（不存在则返回-1）
     */
    private int existsStateSets(List<Set<State>> subsetList, Set<State> subset) {
        for (int i = 0; i < subsetList.size(); i++) {
            if (StateUtil.isEqual(subsetList.get(i), subset)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 判断新状态集是否对应原FA的接受状态
     * 即判断状态集与原FA接受状态集交集是否为空
     */
    private boolean isNewSetAccept(Set<State> subset, Set<State> acceptStateSet) {
        return !StateUtil.intersection(subset, acceptStateSet).isEmpty();
    }

    /**
     * NFA 转 DFA
     * @return DFA
     */
    public DFA toDFA() {
        DFA dfa = new DFA();
        // 获取字母表（剔除epsilon）
        Set<Character> sigma = getSigma();
        Set<State> nfaAcceptStateSet = getAcceptStates();
        // 已处理子集标记
        int flag = 0;
        List<Set<State>> subsetList = new ArrayList<>();
        // DFA 开始状态
        Set<State> startSubset = epsilonClosure(StateUtil.stateToSet(getStartState()));
        subsetList.add(startSubset);
        State dfaStartState = new State(0, isNewSetAccept(startSubset, nfaAcceptStateSet));
        dfa.stateSet.add(dfaStartState);
        dfa.startState = dfaStartState;
        // 子集构造法
        while (flag < subsetList.size()) {
            Set<State> subset = subsetList.get(flag);
            Map<Character, Set<State>> nextStateMap = new HashMap<>();
            for (Character c : sigma) {
                Set<State> newSubset = epsilonClosure(reachable(subset, c));
                if (newSubset.size() == 0) {
                    continue;
                }
                int index = existsStateSets(subsetList, newSubset);
                if (index != -1) {
                    nextStateMap.put(c, StateUtil.stateToSet(dfa.stateSet.get(index)));
                } else {
                    subsetList.add(newSubset);
                    State newState = new State(dfa.stateSet.size(), isNewSetAccept(newSubset, nfaAcceptStateSet));
                    dfa.stateSet.add(newState);
                    nextStateMap.put(c, StateUtil.stateToSet(newState));
                }
            }
            dfa.stateSet.get(flag).setNextState(nextStateMap);
            flag++;
        }
        return dfa;
    }

}
