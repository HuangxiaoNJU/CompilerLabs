package cn.edu.nju;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static cn.edu.nju.Util.END;
import static cn.edu.nju.Util.EPSILON;
import static cn.edu.nju.Util.isTerminal;

public class Parser {

    private static Parser parser = new Parser();

    public static Parser getInstance() {
        return parser;
    }

    private Parser() {}

    /**
     * 解析token识别码
     * @param tokens    token识别码序列
     * @param ppt       LL(1)预测分析表
     * @return          规约序列
     */
    public List<Production> parse(List<String> tokens, ParsingTable ppt) throws ParsingException {
        // 规约序列
        List<Production> res = new ArrayList<>();
        if (!tokens.get(tokens.size() - 1).equals(END)) {
            tokens.add(END);
        }
        Stack<String> stack = new Stack<>();
        stack.push(END);
        stack.push(ppt.getStartVn());
        // 读头
        int readPoint = 0;
        while (readPoint < tokens.size()) {
            if (stack.empty()) {
                throw new ParsingException();
            }
            String peek = stack.peek();
            String vt = tokens.get(readPoint);
            // 栈顶为终结符
            if (isTerminal(peek)) {
                // 栈顶终结符与读头下符号匹配
                if (vt.equals(peek)) {
                    stack.pop();
                    // 读头后移
                    readPoint ++;
                } else {
                    throw new ParsingException();
                }
            }
            // 栈顶为非终结符
            else {
                stack.pop();
                // 读取预测分析表
                Production p = ppt.M(peek, vt);
                res.add(p);
                // 产生式右部逆序压入栈（epsilon不压入栈）
                for (int i = p.right.size() - 1; i >= 0; i--) {
                    String symbol = p.right.get(i);
                    if (!symbol.equals(EPSILON)) {
                        stack.push(p.right.get(i));
                    }
                }
            }
        }
        if (stack.empty()) {
            System.out.println("分析完成");
            return res;
        }
        throw new ParsingException();
    }

}
