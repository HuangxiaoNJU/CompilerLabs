package cn.edu.nju;

import java.util.List;

/**
 * 上下文无关文法产生式
 */
public class Production {

    public int id;
    // 产生式左部
    public String left;
    // 产生式右部
    public List<String> right;

    public Production(int id, String left, List<String> right) {
        this.id = id;
        this.left = left;
        this.right = right;
    }

}
