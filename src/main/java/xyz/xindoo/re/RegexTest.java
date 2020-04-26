package xyz.xindoo.re;


import java.util.List;

public class RegexTest {
    public static void main(String[] args) throws Exception {
        Regex regex = Regex.compile("a\\d(abc|def)d");
        System.out.println(regex.isMatch("axd"));
        System.out.println(regex.isMatch("abcccd"));
        System.out.println(regex.isMatch("ad"));
        System.out.println(regex.isMatch("abcd"));
        System.out.println(regex.isMatch("a3abcd"));
        System.out.println(regex.isMatch("adefd"));
        System.out.println(regex.isMatch("aabcabcabcabcabcabcd"));
//        List<String> res = regex.match("abcdefgaabcdadefd");
//        System.out.println(res.size());
    }
}
