package cn.edu.nju;

public class Util {

    public static final String EPSILON = "0";
    public static final String END = "$";

    /**
     * 判断符号是否为终结符
     * @param symbol    symbol
     * @return          true/false
     */
    public static boolean isTerminal(String symbol) {
        return !(symbol.charAt(0) >= 'A' && symbol.charAt(0) <= 'Z');
    }

}
