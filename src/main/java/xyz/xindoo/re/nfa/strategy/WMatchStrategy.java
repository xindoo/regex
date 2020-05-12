package xyz.xindoo.re.nfa.strategy;

/**
 * 匹配 \w和\W
 */
public class WMatchStrategy extends MatchStrategy {

    public WMatchStrategy(boolean isReverse) {
        this.isReverse = isReverse;
    }

    @Override
    public boolean isMatch(char c, String edge) {
        boolean res = c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9';
        if (isReverse) {
            return !res;
        }
        return res;
    }
}
