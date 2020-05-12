package xyz.xindoo.re.nfa.strategy;

public class EpsilonMatchStrategy extends MatchStrategy {
    @Override
    public boolean isMatch(char c, String edge) {
        return true;
    }
}
