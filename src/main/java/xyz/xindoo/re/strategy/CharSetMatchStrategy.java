package xyz.xindoo.re.strategy;

public class CharSetMatchStrategy implements MatchStrategy{

    private boolean isReverse = false;
    private String charSet = "";
    public CharSetMatchStrategy(String charSet, boolean isReverse) {
        this.charSet = charSet;
        this.isReverse = isReverse;
    }
    public void setReverse(boolean reverse) {
        isReverse = reverse;
    }

    @Override
    public boolean isMatch(char c) {
        boolean res = false;
        for (int i = 0; i < charSet.length(); i++) {
            if ('-' == charSet.charAt(i)) {
                return c >= charSet.charAt(i-1) && c <= charSet.charAt(i+1);
            }
            if (c == charSet.charAt(i)) {
                res = true;
                break;
            }
        }
        if (isReverse)  {
            return !res;
        }
        return res;
    }
}
