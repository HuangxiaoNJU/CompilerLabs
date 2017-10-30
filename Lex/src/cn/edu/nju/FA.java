package cn.edu.nju;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FA {

    protected List<State> stateSet;
    protected State startState;

    protected FA() {
        stateSet = new ArrayList<>();
    }

    protected FA(State startState, List<State> stateSet) {
        this.startState = startState;
        this.stateSet = stateSet;
    }

    /**
     * 获取字母表（不包括epsilon）
     */
    public Set<Character> getSigma() {
        Set<Character> res = new HashSet<>();
        stateSet.forEach(e -> res.addAll(e.getEdgeSet()));
        res.remove('\0');
        return res;
    }

    /**
     * 获取接受状态集
     */
    public Set<State> getAcceptStates() {
        return stateSet
                .stream()
                .filter(State::isAcceptState)
                .collect(Collectors.toSet());
    }

    /**
     * 获取非接受状态集
     */
    public Set<State> getNonAcceptState() {
        return stateSet
                .stream()
                .filter(e -> !e.isAcceptState())
                .collect(Collectors.toSet());
    }

    /**
     * 获取开始状态
     */
    public State getStartState() {
        return startState;
    }

    public void print() {
        System.out.println("Start State:\t" + startState.getStateId());
        System.out.print("Accept State:\t");
        getAcceptStates().forEach(e -> System.out.print(e.getStateId() + " "));
        System.out.println();
        stateSet.forEach(System.out::println);
    }

}
