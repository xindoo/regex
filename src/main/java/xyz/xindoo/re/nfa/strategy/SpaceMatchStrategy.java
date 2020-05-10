package xyz.xindoo.re.nfa.strategy;

public class SpaceMatchStrategy extends MatchStrategy{
    private boolean isReverse;

    public SpaceMatchStrategy(boolean isReverse) {
        this.isReverse = isReverse;
    }

    @Override
    public boolean isMatch(char c, String edge) {
        boolean res = (c == '\f' || c == '\n' || c == '\r' || c == '\t' || c == ' ');
        if (isReverse) {
            return !res;
        }
        return res;
    }
}
