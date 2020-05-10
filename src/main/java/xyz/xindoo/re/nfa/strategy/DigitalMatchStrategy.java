package xyz.xindoo.re.nfa.strategy;

public class DigitalMatchStrategy extends MatchStrategy{
    private boolean isReverse;

    public DigitalMatchStrategy(boolean isReverse) {
        this.isReverse = isReverse;
    }

    @Override
    public boolean isMatch(char c, String edge) {
        boolean res = c >= '0' && c <= '9';
        if (isReverse) {
            return !res;
        }
        return res;
    }
}
