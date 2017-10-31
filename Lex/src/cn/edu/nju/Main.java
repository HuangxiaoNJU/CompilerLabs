package cn.edu.nju;

public class Main {

    public static void main(String[] args) throws RegexException {
        Regex re = new Regex("aa*((bab*a)*(a|b)b*)*");
//        Regex re = new Regex("a(bab*a)*(a|b)b*");
        re.toNFA().toDFA().minimizeDFA().print();
    }

}
