package cn.edu.nju;

import java.util.*;

public class Regex {

    // 正则表达式（字符串中缀表示）
    private String expression;

    // 正则表达式运算符（按优先级从小到大排序）
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
    public String postorder() throws RegexException {
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

    public static void main(String[] args) throws RegexException {
        Regex regex = new Regex("ab(a|b)a*");
        System.out.println(regex.appendConnectDot());
        System.out.println(regex.postorder());
    }

}
