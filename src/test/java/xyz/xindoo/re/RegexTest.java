package xyz.xindoo.re;

public class RegexTest {
    public static void main(String[] args) throws Exception {
        Regex regex = Regex.compile("a.+b");
        System.out.println(regex.isMatch("abb"));
        System.out.println(regex.isMatch("a1123b"));
        System.out.println(regex.isMatch("abcccd"));
        System.out.println(regex.isMatch("ad"));
        System.out.println(regex.isMatch("abcd"));
        System.out.println(regex.isMatch("a3abcd"));
        System.out.println(regex.isMatch("a33333defd"));
        System.out.println(regex.isMatch("aabcabcabcabcabcabcdb"));
    }
}
