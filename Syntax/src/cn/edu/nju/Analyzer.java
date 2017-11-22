package cn.edu.nju;

import java.util.*;

public class Analyzer {

    private static final String EPSILON = "0";

    private static Analyzer analyzer;

    public static Analyzer getInstance() {
        if (analyzer == null) {
            analyzer = new Analyzer();
        }
        return analyzer;
    }

    private Analyzer() {}

    /**
     * 预处理
     * 判断是否含有左递归
     * @param productions   产生式
     */
    private void preprocess(List<Production> productions) throws GrammarException {
        for (Production p : productions) {
            String first = p.right.get(0);
            if (p.left.equals(first)) {
                throw new GrammarException("文法产生式含有左递归");
            }
        }
    }

    /**
     * 判断符号是否为终结符
     * @param symbol    symbol
     * @return          true/false
     */
    private boolean isTerminal(String symbol) {
        return !(symbol.charAt(0) >= 'A' && symbol.charAt(0) <= 'Z');
    }

    /**
     * First集合
     * @param vn            Non-terminal symbol
     * @param productions   productions
     * @return              First(Vn)
     */
    private Set<String> first(String vn, List<Production> productions) {
        Set<String> res = new HashSet<>();
        for (Production p : productions) {
            if (p.left.equals(vn)) {
                for (String symbol : p.right) {
                    // 遇到非终结符结束
                    if (isTerminal(symbol)) {
                        res.add(symbol);
                        break;
                    }
                    if (symbol.equals(vn)) {
                        continue;
                    }
                    Set<String> terminals = first(symbol, productions);
                    res.addAll(terminals);
                    if (!terminals.contains(EPSILON)) {
                        break;
                    }
                }
            }
        }
        return res;
    }

    private Set<String> follow(String vn, Set<String> set, List<Production> productions) {
        Set<String> res = new HashSet<>();
        // 开始符follow集中加入$
        if (vn.equals(productions.get(0).left)) {
            res.add("$");
        }
        for (Production p : productions) {
            if (!p.right.contains(vn)) {
                continue;
            }
            for (int i = 0; i < p.right.size(); i++) {
                // 寻找产生式右部的Vn
                if (p.right.get(i).equals(vn)) {
                    // 判断产生式Vn后是否能推出epsilon
                    boolean isPCanEndWithVn = true;
                    // Vn不在产生式最右部
                    for (int j = i + 1; j < p.right.size(); j++) {
                        String symbol = p.right.get(j);
                        if (isTerminal(symbol)) {
                            res.add(symbol);
                            isPCanEndWithVn = false;
                            break;
                        }
                        Set<String> first = first(symbol, productions);
                        if (first.contains(EPSILON)) {
                            res.addAll(first);
                            res.remove(EPSILON);
                        } else {
                            res.addAll(first);
                            isPCanEndWithVn = false;
                            break;
                        }
                    }
                    if (isPCanEndWithVn && !set.contains(p.left)) {
                        set.add(p.left);
                        res.addAll(follow(p.left, set, productions));
                    }
                }
            }
        }
        return res;
    }

    /**
     * Follow集合
     * @param vn            Non-terminal symbol
     * @param productions   productions
     * @return              Follow(Vn)
     */
    private Set<String> follow(String vn, List<Production> productions) {
        return follow(vn, new HashSet<String>(){{ add(vn); }}, productions);
    }

    /**
     * 构造PPT
     * @param productions   产生式
     */
    public void parsingTable(List<Production> productions) throws GrammarException {
        preprocess(productions);

//        Map<String, Set<String>> firstSet = new HashMap<>();
//        for (Production p : productions) {
//            if (!firstSet.containsKey(p.left)) {
//                firstSet.put(p.left, first(p.left, productions));
//            }
//        }

        Set<String> set = follow("D'", productions);
        set.forEach(System.out::println);
    }

}
