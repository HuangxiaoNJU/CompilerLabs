package cn.edu.nju;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
                    .filter(s -> !terminalList.contains(s) && Analyzer.isTerminal(s))
                    .collect(Collectors.toList())
            );
        }
        terminalList.remove(Analyzer.EPSILON);
        terminalList.add(Analyzer.END);
        ppt = new Production[nonTerminalList.size()][terminalList.size()];
    }

    public Production M(String vn, String vt) {
        int vnIndex = nonTerminalList.indexOf(vn);
        int vtIndex = terminalList.indexOf(vt);
        return ppt[vnIndex][vtIndex];
    }

    public void setPPT(String vn, String vt, Production p) throws GrammarException {
        int vnIndex = nonTerminalList.indexOf(vn);
        int vtIndex = terminalList.indexOf(vt);
        if (ppt[vnIndex][vtIndex] != null) {
            throw new GrammarException("该文法不是LL(1)文法");
        }
        ppt[vnIndex][vtIndex] = p;
    }

}
