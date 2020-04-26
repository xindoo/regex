package xyz.xindoo.re.strategy;

public class EpsilonMatchStrategy implements MatchStrategy{
    private int low = 0;
    private int high = Integer.MAX_VALUE;
    @Override
    public boolean isMatch(char c) {
        return true;
    }
}
