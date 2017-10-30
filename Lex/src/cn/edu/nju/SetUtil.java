package cn.edu.nju;

import java.util.HashSet;
import java.util.Set;

/**
 * 状态集工具类
 */
public class SetUtil {

    /**
     * 判断两个状态集中状态是否完全相同
     * @param set1  state set1
     * @param set2  state set2
     * @return      boolean
     */
    public static boolean isEqual(Set<State> set1, Set<State> set2) {
        return set1.containsAll(set2) && set2.containsAll(set1);
    }

    /**
     * 封装单个状态为状态集
     * @param state single state
     * @return      Set
     */
    public static Set<State> toSet(State state) {
        return new HashSet<State>(){{ add(state); }};
    }

    /**
     * 求两个状态集交集
     * @return Intersection of set1 and set2
     */
    public static Set<State> intersection(Set<State> set1, Set<State> set2) {
        Set<State> res = new HashSet<>(set1);
        res.retainAll(set2);
        return res;
    }

}
