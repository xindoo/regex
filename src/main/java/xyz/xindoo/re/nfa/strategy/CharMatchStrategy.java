package xyz.xindoo.re.nfa.strategy;

public class CharMatchStrategy extends MatchStrategy{
    @Override
    public boolean isMatch(char c, String edge) {
        return edge.charAt(0) == c;
    }
}
