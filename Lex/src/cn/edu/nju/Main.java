package cn.edu.nju;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static String RE_FILE_NAME = "re.txt";
    private static String INPUT_FILE_NAME = "input.txt";
    private static String OUTPUT_FILE_NAME = "output.txt";

    private static List<DFA> dfaList = new ArrayList<>();

    private static void init() throws IOException {
        File file = new File(RE_FILE_NAME);
        BufferedReader br = new BufferedReader(new FileReader(file));

        String line;
        while ((line = br.readLine()) != null) {
            // 判断RE是否为空
            if (line.equals("") || line.contains(" ")) {
                continue;
            }
            try {
                dfaList.add(new Regex(line).toNFA().toDFA().minimizeDFA());
            } catch (RegexException e) {
                System.out.println(line + " regular expression error!");
            }
        }

        br.close();
    }

    private static String getToken(String word) {
        for (int i = 0; i < dfaList.size(); i++) {
            DFA dfa = dfaList.get(i);
            if (dfa.isMatch(word)) {
                return "<" + i + ", " + word + ">";
            }
        }
        return "<ERROR, " + word + ">";
    }

    private static void analyze() throws IOException {
        File inputFile = new File(INPUT_FILE_NAME);
        FileWriter fw = new FileWriter(OUTPUT_FILE_NAME);
        BufferedReader br = new BufferedReader(new FileReader(inputFile));
        String line;
        while ((line = br.readLine()) != null) {
            String[] words = line.split("\\s+");
            for (String word : words) {
                fw.append(getToken(word)).append("\n");
            }
        }
        fw.flush();
        fw.close();
    }

    public static void main(String[] args) {
        try {
            init();
            analyze();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("File error!");
        }
    }

}
