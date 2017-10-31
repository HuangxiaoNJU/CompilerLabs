package cn.edu.nju;

import java.util.*;

public class DFA extends FA {

    private Set<State> reachable(Set<State> states, char c) {
        Set<State> res = new HashSet<>();
        states.forEach(e -> res.addAll(e.next(c)));
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
            dfs(res, state.next('\0'));
        }
    }

    /**
     * 求状态集epsilon闭包
     */
    private Set<State> epsilonClosure(Set<State> states) {
        Set<State> res = new HashSet<>();
        dfs(res, states);
        return res;
    }

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
        Set<State> startSubset = epsilonClosure(StateUtil.stateToSet(nfa.getStartState()));
        subsetList.add(startSubset);
        State dfaStartState = new State(0, isNewSetAccept(startSubset, nfaAcceptStateSet));
        stateSet.add(dfaStartState);
        startState = dfaStartState;
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
                    nextStateMap.put(c, StateUtil.stateToSet(stateSet.get(index)));
                } else {
                    subsetList.add(newSubset);
                    State newState = new State(stateSet.size(), isNewSetAccept(newSubset, nfaAcceptStateSet));
                    stateSet.add(newState);
                    nextStateMap.put(c, StateUtil.stateToSet(newState));
                }
            }
            stateSet.get(flag).setNextState(nextStateMap);
            flag++;
        }
    }

    /**
     * 获得状态所在组号
     */
    private int getGroupNum(List<Set<State>> pi, State state) {
        for (Set<State> states : pi) {
            if (states.contains(state)) {
                return pi.indexOf(states);
            }
        }
        return -1;
    }

    private void addStateToMap(Map<Integer, Set<State>> divideMap, int key, State state) {
        if (divideMap.containsKey(key)) {
            divideMap.get(key).add(state);
        } else {
            divideMap.put(key, StateUtil.stateToSet(state));
        }
    }

    /**
     * DFA 最小化
     */
    public void minimizeDFA() {
        Set<Character> sigma = getSigma();
        List<Set<State>> pi = new ArrayList<>();
        List<Set<State>> newPI = new ArrayList<>();
        pi.add(getNonAcceptState());
        pi.add(getAcceptStates());
        while (true) {
            for (Set<State> states : pi) {
                // 组内已只有一个状态
                if (states.size() == 1) {
                    newPI.add(states);
                    continue;
                }
                boolean isDivided = false;
                for (Character c : sigma) {
                    // 分组map，其中key为组号（组所在下标），value为状态集
                    Map<Integer, Set<State>> divideMap = new HashMap<>();
                    // 状态集中状态依次分组
                    for (State state : states) {
                        if (state.next(c).isEmpty()) {
                            addStateToMap(divideMap, -1, state);
                        } else {
                            int groupNum = getGroupNum(pi, StateUtil.setToState(state.next(c)));
                            addStateToMap(divideMap, groupNum, state);
                        }
                    }
                    if (divideMap.keySet().size() >= 2) {
                        newPI.addAll(divideMap.values());
                        isDivided = true;
                        break;
                    }
                }
                if (!isDivided) {
                    newPI.add(states);
                }
            }
            // 不可再划分，循环结束
            if (pi.size() == newPI.size()) {
                break;
            }
            pi = new ArrayList<>(newPI);
            newPI.clear();
        }
        generateMinimalDFA(pi, sigma);
    }

    private void generateMinimalDFA(List<Set<State>> pi, Set<Character> sigma) {
        List<State> minimalDFAStates = new ArrayList<>();
        Set<State> originalAcceptSet = getAcceptStates();
        // 初始化新状态，同时判断新状态是否为开始状态或终态
        for (int i = 0; i < pi.size(); i++) {
            Set<State> group = pi.get(i);
            State newState = new State(i, isNewSetAccept(group, originalAcceptSet));
            minimalDFAStates.add(newState);
            if (group.contains(startState)) {
                startState = newState;
            }
        }
        // 建立新状态转换图
        for (int i = 0; i < pi.size(); i++) {
            State oneStateInGroup = StateUtil.setToState(pi.get(i));
            for (Character c : sigma) {
                Set<State> nextStates = oneStateInGroup.next(c);
                if (!nextStates.isEmpty()) {
                    int groupNum = getGroupNum(pi, StateUtil.setToState(nextStates));
                    minimalDFAStates.get(i).addNextState(c, minimalDFAStates.get(groupNum));
                }
            }
        }
        stateSet = minimalDFAStates;
    }

    /**
     * 测试main
     */
    public static void main(String[] args) throws RegexException {
        NFA nfa = new Regex("(a|b)*abb(a|b)*").toNFA();
        DFA dfa = new DFA(nfa);
        dfa.minimizeDFA();
        dfa.print();
//        Set<State> states = dfa.epsilonClosure(
//            nfa.getStateSet().stream().filter(e -> e.getStateId() == 2 || e.getStateId() == 6).collect(Collectors.stateToSet())
//        );

//        states.forEach(e -> System.out.print(e.getStateId() + " "));
    }

}
