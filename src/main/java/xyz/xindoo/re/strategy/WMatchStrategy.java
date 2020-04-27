package xyz.xindoo.re.strategy;

/**
 * 匹配 \w和\W
 * */
public class WMatchStrategy implements MatchStrategy{
    boolean isReverse;
    public WMatchStrategy(boolean isReverse) {
        this.isReverse = isReverse;
    }
    @Override
    public boolean isMatch(char c) {
        boolean res = c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9';
        if (isReverse) {
            return !res;
        }
        return res;
    }
}
