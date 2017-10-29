package cn.edu.nju;

import java.util.*;

public class Regex {

    // 正则表达式（字符串中缀表示）
    private String expression;

    // 正则表达式运算符（优先级从小到大排序）
    private static final List<Character> OPERATION_CHAR;
    static {
        OPERATION_CHAR = Arrays.asList('|', '.', '*');
    }

    public Regex(String expression) {
        this.expression = expression;
    }

    /**
     * 预处理
     * 添加连接符号'.'
     * 算法：
     * '(' '|' '.'之后无连接符
     * '*' ')' '|' '.'之前无连接符
     */
    private String appendConnectDot() {
        StringBuilder sb = new StringBuilder();
        List<Character> temp = new ArrayList<>(Arrays.asList('|', '*', '.', ')'));
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (c == '(' || c == '|' || c == '.') {
                sb.append(c);
                continue;
            }
            if (i == expression.length() - 1 || temp.contains(expression.charAt(i + 1))) {
                sb.append(c);
            } else {
                sb.append(c).append('.');
            }
        }
        return sb.toString();
    }

    /**
     * 正则表达式中缀转后缀
     */
    private String postorder() throws RegexException {
        String inorder = appendConnectDot() + '$';
        StringBuilder sb = new StringBuilder();
        Stack<Character> stack = new Stack<>();
        for (int i = 0; i < inorder.length(); i++) {
            char c = inorder.charAt(i);
            switch (c) {
                case '(':
                    stack.push(c);
                    break;
                case ')':
                    while (!stack.empty() && stack.peek() != '(') {
                        sb.append(stack.pop());
                    }
                    if (stack.empty() || stack.peek() != '(') {
                        throw new RegexException();
                    }
                    stack.pop();
                    break;
                case '$':
                    while (!stack.empty()) {
                        if (stack.peek() == '(') {
                            throw new RegexException();
                        }
                        sb.append(stack.pop());
                    }
                    break;
                default:
                    if (OPERATION_CHAR.contains(c)) {
                        int priorityLevel = OPERATION_CHAR.indexOf(c);
                        while(!stack.empty() && OPERATION_CHAR.indexOf(stack.peek()) >= priorityLevel) {
                            sb.append(stack.pop());
                        }
                        stack.push(c);
                    } else {
                        sb.append(c);
                    }
                    break;
            }

        }
        return sb.toString();
    }

    /**
     * RE to NFA
     * @return NFA
     */
    public NFA toNFA() throws RegexException {
        // RE转换为后缀表示
        String postorder = postorder();
        Stack<NFA> stack = new Stack<>();
        int stateId = 0;
        for (int i = 0; i < postorder.length(); i++) {
            char c = postorder.charAt(i);
            if (c == '.') {
                NFA nfa2 = stack.pop();
                NFA nfa1 = stack.pop();
                nfa1.connectNFA(nfa2);
                stack.push(nfa1);
            } else if (c == '|') {
                NFA nfa2 = stack.pop();
                NFA nfa1 = stack.pop();
                State newStart = new State(stateId++);
                State newAccept = new State(stateId++, true);
                nfa1.orNFA(nfa2, newStart, newAccept);
                stack.push(nfa1);
            } else if (c == '*') {
                NFA nfa = stack.pop();
                State newStart = new State(stateId++);
                State newAccept = new State(stateId++, true);
                nfa.closureNFA(newStart, newAccept);
                stack.push(nfa);
            } else {
                // 非运算符，生成DFA
                State state1 = new State(stateId++);
                State state2 = new State(stateId++, true);
                state1.addNextState(c, state2);
                NFA nfa = new NFA(state1, Arrays.asList(state1, state2));
                stack.push(nfa);
            }
        }
        return stack.peek();
    }

    public static void main(String[] args) throws RegexException {
        Regex regex = new Regex("ab(a|b)a*");
        System.out.println(regex.appendConnectDot());
        System.out.println(regex.postorder());
    }

}
