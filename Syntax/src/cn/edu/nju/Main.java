package cn.edu.nju;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final String CFG_FILE_NAME = "cfg.txt";
    private static final String INPUT_FILE_NAME = "input.txt";

    /**
     * 根据大小写字母拆分字符串
     * （提取终结符和非终结符）
     */
    private static List<String> splitByCase(String s) {
        List<String> res = new ArrayList<>();
        boolean isUpperCase = 'A' <= s.charAt(0) && s.charAt(0) <= 'Z';
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            if ('A' <= c && c <= 'Z') {
                if (!isUpperCase) {
                    isUpperCase = true;
                    res.add(sb.toString());
                    sb = new StringBuilder();
                }
                res.add(String.valueOf(c));
            } else {
                isUpperCase = false;
                sb.append(c);
            }
        }
        if (sb.length() != 0) {
            res.add(sb.toString());
        }
        return res;
    }

    private static List<Production> getProductions() throws IOException {
        List<Production> res = new ArrayList<>();

        BufferedReader br = new BufferedReader(new FileReader(CFG_FILE_NAME));
        String line;
        int id = 1;
        while ((line = br.readLine()) != null) {
            line = line.replaceAll("\\s", "");
            String[] info = line.split("->|\\|");
            for (int i = 1; i < info.length; i++) {
                res.add(new Production(id++, info[0], splitByCase(info[i])));
            }
        }
        return res;
    }

    public static void main(String[] args) throws IOException {
        List<Production> productions = getProductions();
        // 加入0号产生式
        String start = productions.get(0).left;
        List<String> right = new ArrayList<String>(){{add(start);}};
        productions.add(0, new Production(0, "S'", right));
        // TODO
    }

}
