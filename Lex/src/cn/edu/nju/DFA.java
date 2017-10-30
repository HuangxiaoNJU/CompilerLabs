package cn.edu.nju;

import java.util.*;

public class DFA extends FA {

    private int existsStateSets(List<Set<State>> subsetList, Set<State> subset) {
        for (int i = 0; i < subsetList.size(); i++) {
            if (SetUtil.isEqual(subsetList.get(i), subset)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 判断子集是否对应DFA的接受状态
     */
    private boolean isSubsetBeAcceptState(Set<State> subset, Set<State> nfaAcceptStateSet) {
        return SetUtil.intersection(subset, nfaAcceptStateSet).size() > 0;
    }

    /**
     * 构造方法
     * NFA转DFA
     */
    public DFA(NFA nfa) {
        super();
        // 获取字母表（剔除epsilon）
        Set<Character> sigma = nfa.getSigma();
        Set<State> nfaAcceptStateSet = nfa.getAcceptStates();
        // 已处理子集标记
        int flag = 0;
        List<Set<State>> subsetList = new ArrayList<>();
        // DFA 开始状态
        Set<State> startSubset = epsilonClosure(SetUtil.toSet(nfa.getStartState()));
        subsetList.add(startSubset);
        State dfaStartState = new State(0, isSubsetBeAcceptState(startSubset, nfaAcceptStateSet));
        stateSet.add(dfaStartState);
        startState = dfaStartState;
        // 子集构造法
        while (flag < subsetList.size()) {
            Set<State> subset = subsetList.get(flag);
            Map<Character, Set<State>> nextStateMap = new HashMap<>();
            for (Character c : sigma) {
                Set<State> newSubset = epsilonClosure(reachable(subset, c));
                int index = existsStateSets(subsetList, newSubset);
                if (index != -1) {
                    nextStateMap.put(c, SetUtil.toSet(stateSet.get(index)));
                } else {
                    subsetList.add(newSubset);
                    State newState = new State(stateSet.size(), isSubsetBeAcceptState(newSubset, nfaAcceptStateSet));
                    stateSet.add(newState);
                    nextStateMap.put(c, SetUtil.toSet(newState));
                }
            }
            stateSet.get(flag).setNextState(nextStateMap);
            flag++;
        }
    }

    private Set<State> reachable(Set<State> states, char c) {
        Set<State> res = new HashSet<>();
        states.forEach(e -> {
            if (e.getNextState().containsKey(c)) {
                res.addAll(e.getNextState().get(c));
            }
        });
        return res;
    }

    /**
     * 深搜epsilon闭包
     */
    private void dfs(Set<State> res, Set<State> toFind) {
        for (State state : toFind) {
            if (res.contains(state)) {
                return;
            }
            res.add(state);
            if (state.getNextState().containsKey('\0')) {
                dfs(res, state.getNextState().get('\0'));
            }
        }
    }

    private Set<State> epsilonClosure(Set<State> states) {
        Set<State> res = new HashSet<>();
        dfs(res, states);
        return res;
    }

    /**
     * 测试main
     */
    public static void main(String[] args) throws RegexException {
        NFA nfa = new Regex("(a*|b*)*").toNFA();
        DFA dfa = new DFA(nfa);
        dfa.print();
//        Set<State> states = dfa.epsilonClosure(
//            nfa.getStateSet().stream().filter(e -> e.getStateId() == 2 || e.getStateId() == 6).collect(Collectors.toSet())
//        );

//        states.forEach(e -> System.out.print(e.getStateId() + " "));
    }

}
