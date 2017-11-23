package cn.edu.nju;

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

    public void parse(List<String> tokens, ParsingTable ppt) throws ParsingException {
        if (!tokens.get(tokens.size() - 1).equals(END)) {
            tokens.add(END);
        }
        Stack<String> stack = new Stack<>();
        stack.push(END);
        stack.push(ppt.getStartVn());
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
        } else {
            throw new ParsingException();
        }
    }

}
