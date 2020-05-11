package xyz.xindoo.re;

public class RegexTest {
    public static void main(String[] args) throws Exception {
        Regex regex = Regex.compile("a(b|c)*");
//        Regex regex = Regex.compile("a\\s+.+b");
        System.out.println(regex.isMatch("ac"));
        System.out.println(regex.isMatch("ac"));
        System.out.println(regex.isMatch("a"));
        System.out.println(regex.isMatch("a   bcccdb"));
        System.out.println(regex.isMatch("ab"));
        System.out.println(regex.isMatch("abcd"));
        System.out.println(regex.isMatch("a3abcd"));
        System.out.println(regex.isMatch("a33333defd"));
        System.out.println(regex.isMatch("aabcabcabcabcabcabcdb"));

        System.out.println("*********");
        System.out.println(regex.isDFAMatch("ac"));
        System.out.println(regex.isDFAMatch("ac"));
        System.out.println(regex.isDFAMatch("a"));
        System.out.println(regex.isDFAMatch("a   bcccdb"));
        System.out.println(regex.isDFAMatch("ab"));
        System.out.println(regex.isDFAMatch("abcd"));
        System.out.println(regex.isDFAMatch("a3abcd"));
        System.out.println(regex.isDFAMatch("a33333defd"));
        System.out.println(regex.isDFAMatch("aabcabcabcabcabcabcdb"));
    }
}
