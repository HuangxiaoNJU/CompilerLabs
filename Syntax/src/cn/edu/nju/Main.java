package cn.edu.nju;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    private static Analyzer analyzer = Analyzer.getInstance();

    private static final String CFG_FILE_NAME = "cfg.txt";
    private static final String INPUT_FILE_NAME = "input.txt";

    /**
     * 根据空白字符拆分字符串
     * （提取产生式右部元素）
     */
    private static List<String> splitByWhitespace(String str) {
        return Arrays.stream(str.trim().split("\\s"))
                .filter(s -> !s.matches("\\s") && !s.equals(""))
                .collect(Collectors.toList());
    }

    /**
     * 读取cfg.txt
     * 返回所有产生式
     * @return  产生式列表
     */
    private static List<Production> getProductions() throws IOException, GrammarException {
        List<Production> res = new ArrayList<>();

        BufferedReader br = new BufferedReader(new FileReader(CFG_FILE_NAME));
        String line;
        int id = 1;
        while ((line = br.readLine()) != null) {
            // 跳过空白行
            if (line.equals("") || line.matches("\\s")) {
                continue;
            }
            String[] info = line.split("->|\\|");
            if (info.length < 2) {
                throw new GrammarException("产生式格式错误");
            }
            for (int i = 1; i < info.length; i++) {
                res.add(new Production(id++, info[0].trim(), splitByWhitespace(info[i])));
            }
        }
        return res;
    }

    public static void main(String[] args) throws IOException {
        try {
            List<Production> productions = getProductions();
//            productions.forEach(p -> {
//                System.out.print(p.id + ":\t" + p.left + " -> ");
//                p.right.forEach(r -> System.out.print(r + " "));
//                System.out.println();
//            });

            analyzer.parsingTable(productions);
        } catch (GrammarException e) {
            System.out.println(e.getMessage());
        }
    }

}
