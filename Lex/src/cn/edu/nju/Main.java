package cn.edu.nju;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final String RE_FILE_NAME = "re.txt";
    private static final String INPUT_FILE_NAME = "input.txt";
    private static final String OUTPUT_FILE_NAME = "output.txt";

    private static List<DFA> dfaList = new ArrayList<>();
    private static List<String> typeList = new ArrayList<>();

    private static void init() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(RE_FILE_NAME));

        String line;
        while ((line = br.readLine()) != null) {
            String[] info = line.split("\\s+");
            if (info.length <= 1) {
                System.out.println("Warning:\t" + line + "\nDefinition error！");
                continue;
            }
            String type = info[0];
            String re = info[1];
            // 判断RE是否包含空字符
            if (re.matches("\\s*")) {
                System.out.println("Warning:\t" + line + "\nDefinition error！");
                continue;
            }
            DFA dfa;
            try {
                dfa = new Regex(re).toNFA().toDFA().minimizeDFA();
            } catch (RegexException e) {
                System.out.println("Warning:\t" + re + "\n" + e.getMessage());
                continue;
            }
            dfaList.add(dfa);
            typeList.add(type);
        }
        br.close();
    }

    private static String getToken(String word) {
        for (int i = 0; i < dfaList.size(); i++) {
            if (dfaList.get(i).isMatch(word)) {
                return "<" + typeList.get(i) + ", " + word + ">";
            }
        }
        return  "<ERROR, " + word + ">";
    }

    private static void analyze() throws IOException {
        File inputFile = new File(INPUT_FILE_NAME);
        FileWriter fw = new FileWriter(OUTPUT_FILE_NAME);
        BufferedReader br = new BufferedReader(new FileReader(inputFile));
        String line;
        while ((line = br.readLine()) != null) {
            String[] words = line.split("\\s+");
            for (String word : words) {
                if (!word.matches("\\s*")) {
                    fw.append(getToken(word)).append("\n");
                }
            }
        }
        fw.flush();
        fw.close();
    }

    public static void main(String[] args) throws RegexException {
        try {
            init();
            analyze();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("File error!");
        }
//        new Regex("(a|(a|(a|b*))*)*(a|b*)").toNFA().toDFA().minimizeDFA().print();
    }

}
