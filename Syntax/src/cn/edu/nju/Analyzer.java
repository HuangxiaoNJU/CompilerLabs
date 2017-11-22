package cn.edu.nju;

import java.util.*;

public class Analyzer {

    public static final String EPSILON = "0";
    public static final String END = "$";

    private static Analyzer analyzer = new Analyzer();

    public static Analyzer getInstance() {
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
    public static boolean isTerminal(String symbol) {
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
        if (isTerminal(vn)) {

        }
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

    private Set<String> first(List<String> symbols, List<Production> productions) {
        Set<String> res = new HashSet<>();
        for (String symbol : symbols) {
            if (isTerminal(symbol)) {
                res.add(symbol);
                break;
            }
            Set<String> firstSet = first(symbol, productions);
            res.addAll(firstSet);
            if (!firstSet.contains(EPSILON)) {
                break;
            }
        }
        return res;
    }

    private Set<String> follow(String vn, Set<String> set, List<Production> productions) {
        Set<String> res = new HashSet<>();
        // 开始符follow集中加入$
        if (vn.equals(productions.get(0).left)) {
            res.add(END);
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
    public ParsingTable parsingTable(List<Production> productions) throws GrammarException {
        preprocess(productions);

        // 构造PPT
        ParsingTable parsingTable = new ParsingTable(productions);
        for (Production p : productions) {
            Set<String> firstSet = first(p.right, productions);
            for (String vt : firstSet) {
                if (!vt.equals(EPSILON)) {
                    parsingTable.setPPT(p.left, vt, p);
                }
            }
            if (firstSet.contains(EPSILON)) {
                Set<String> followSet = follow(p.left, productions);
                for (String vt : followSet) {
                    parsingTable.setPPT(p.left, vt, p);
                }
            }
        }

        return parsingTable;
    }

}
