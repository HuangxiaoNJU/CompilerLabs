package cn.edu.nju;

import java.util.*;

public class DFA extends FA {

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
    public DFA minimizeDFA() {
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
                    // 分组map，其中key为组号（组所在下标），value为分到该组的状态集
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
        return this;
    }

    private void generateMinimalDFA(List<Set<State>> pi, Set<Character> sigma) {
        List<State> minimalDFAStates = new ArrayList<>();
        // 初始化新状态，同时判断新状态是否为开始状态或终态
        for (int i = 0; i < pi.size(); i++) {
            Set<State> group = pi.get(i);
            State newState = new State(i, StateUtil.setToState(group).isAcceptState());
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

}
