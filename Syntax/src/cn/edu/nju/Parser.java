package cn.edu.nju;

import java.util.List;

public class Parser {

    private static Parser parser = new Parser();

    public static Parser getInstance() {
        return parser;
    }

    private Parser() {}

    public void parse(List<String> tokens, ParsingTable ppt) {

    }

}
