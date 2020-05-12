package xyz.xindoo.re.common;

public class Reader {
    private int cur = 0;
    private char[] chars;
    public Reader(String regex) {
        this.chars = regex.toCharArray();
    }
    public char peak() {
        if (cur == chars.length) {
            return '\0';
        }
        return chars[cur];
    }

    public char next() {
        if (cur == chars.length) {
            return '\0';
        }
        return chars[cur++];
    }

    public boolean hasNext() {
        return cur < chars.length;
    }

    public String getSubRegex(Reader reader) {
        int cntParem = 1;
        String regex = "";
        while (reader.hasNext()) {
            char ch = reader.next();
            if (ch == '(') {
                cntParem++;
            } else if (ch == ')') {
                cntParem--;
                if (cntParem == 0) {
                    break;
                } else {
                }
            }
            regex += ch;
        }
        return regex;
    }

    public String getRemainRegex(Reader reader) {
        String regex = "";
        while (reader.hasNext()) {
            regex += reader.next();
        }
        return regex;
    }
}
