package xyz.xindoo.re.strategy;

public class DigitalMatchStrategy implements MatchStrategy{
    private boolean isReverse;

    public DigitalMatchStrategy(boolean isReverse) {
        this.isReverse = isReverse;
    }

    @Override
    public boolean isMatch(char c) {
        boolean res = c >= '0' && c <= '9';
        if (isReverse) {
            return !res;
        }
        return res;
    }
}
