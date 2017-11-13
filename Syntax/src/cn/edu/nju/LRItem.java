package cn.edu.nju;

import java.util.List;

/**
 * LR项
 */
public class LRItem {

    // 产生式左部
    public String left;
    // 产生式右部
    public List<String> right;
    // .位置，开始为0
    public int dotPos;
    // 预测符
    public List<String> predicts;

    public LRItem(String production) {

    }

}
