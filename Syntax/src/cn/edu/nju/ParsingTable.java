package cn.edu.nju;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static cn.edu.nju.Util.END;
import static cn.edu.nju.Util.EPSILON;
import static cn.edu.nju.Util.isTerminal;

/**
 * 预测分析表
 */
public class ParsingTable {

    private List<String> nonTerminalList = new ArrayList<>();
    private List<String> terminalList = new ArrayList<>();

    private Production[][] ppt;

    public ParsingTable(List<Production> productions) {
        for (Production p : productions) {
            if (!nonTerminalList.contains(p.left)) {
                nonTerminalList.add(p.left);
            }
            terminalList.addAll(
                    p.right.stream()
                    .filter(s -> !terminalList.contains(s) && isTerminal(s))
                    .collect(Collectors.toList())
            );
        }
        terminalList.remove(EPSILON);
        terminalList.add(END);
        ppt = new Production[nonTerminalList.size()][terminalList.size()];
    }

    public Production M(String vn, String vt) {
        int vnIndex = nonTerminalList.indexOf(vn);
        int vtIndex = terminalList.indexOf(vt);
        return ppt[vnIndex][vtIndex];
    }

    /**
     * 设置PPT表项
     * @param vn    Vn
     * @param vt    Vt
     * @param p     production
     * @throws GrammarException 冲突
     */
    public void setPPT(String vn, String vt, Production p) throws GrammarException {
        int vnIndex = nonTerminalList.indexOf(vn);
        int vtIndex = terminalList.indexOf(vt);
        if (ppt[vnIndex][vtIndex] != null) {
            throw new GrammarException("该文法不是LL(1)文法");
        }
        ppt[vnIndex][vtIndex] = p;
    }

    /**
     * 获取文法开始符
     * @return      Vn
     */
    public String getStartVn() {
        return nonTerminalList.get(0);
    }

    public void print() {
        for (int i = 0; i < ppt.length; i++) {
            for (int j = 0; j < ppt[0].length; j++) {
                if (ppt[i][j] != null) {
                    String vn = nonTerminalList.get(i);
                    String vt = terminalList.get(j);
                    System.out.println("M[ " + vn + ", " + vt + " ]\t = \t" + ppt[i][j]);
                }
            }
        }
    }

}
