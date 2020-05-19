package xyz.xindoo.re;

import java.util.List;

public class RegexTest {
    public static void main(String[] args) throws Exception {
        Regex regex = Regex.compile("a(b|c)*c");
        List<String> res = regex.match("aabacabbbcaccc");
        regex.printNfa();
        System.out.println("");
        regex.printDfa();
//        Regex regex = Regex.compile("a\\s+.+b");
        System.out.println(regex.isMatch("ac"));
        System.out.println(regex.isMatch("acc"));
        System.out.println(regex.isMatch("a"));
        System.out.println(regex.isMatch("a   bcccdb"));
        System.out.println(regex.isMatch("ab"));
        System.out.println(regex.isMatch("abcd"));
        System.out.println(regex.isMatch("a3abcd"));
        System.out.println(regex.isMatch("a33333defd"));
        System.out.println(regex.isMatch("aabcabcabcabcabcabcdb"));

        System.out.println("*********");
        System.out.println(regex.isMatch("ac", 1));
        System.out.println(regex.isMatch("acc", 1));
        System.out.println(regex.isMatch("a", 1));
        System.out.println(regex.isMatch("a   bcccdb", 1));
        System.out.println(regex.isMatch("ab", 1));
        System.out.println(regex.isMatch("abcd", 1));
        System.out.println(regex.isMatch("a3abcd", 1));
        System.out.println(regex.isMatch("a33333defd", 1));
        System.out.println(regex.isMatch("aabcabcabcabcabcabcdb", 1));
    }
}
