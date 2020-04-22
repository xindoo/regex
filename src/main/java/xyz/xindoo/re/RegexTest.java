package xyz.xindoo.re;


public class RegexTest {
    public static void main(String[] args) throws Exception {
        Regex regex = Regex.compile("a(abc|def)d");
        System.out.println(regex.isMatch("axd"));
        System.out.println(regex.isMatch("abcccd"));
        System.out.println(regex.isMatch("ad"));
        System.out.println(regex.isMatch("abcd"));
        System.out.println(regex.isMatch("aabcd"));
        System.out.println(regex.isMatch("adefd"));
        System.out.println(regex.isMatch("aabcabcabcabcabcabcd"));
    }
}
