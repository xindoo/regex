package xyz.xindoo.re;


import java.util.List;

public class RegexTest {
    public static void main(String[] args) throws Exception {
        Regex regex = Regex.compile("a\\s?.+b");
        System.out.println(regex.isMatch("abb"));
        System.out.println(regex.isMatch("a 1123b"));
        System.out.println(regex.isMatch("a   bcccd"));
        System.out.println(regex.isMatch("ad"));
        System.out.println(regex.isMatch("abcd"));
        System.out.println(regex.isMatch("a3abcd"));
        System.out.println(regex.isMatch("a33333defd"));
        System.out.println(regex.isMatch("aabcabcabcabcabcabcdb"));
    }
}
