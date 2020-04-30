package xyz.xindoo.re.nfa.strategy;

public class DotMatchStrategy extends MatchStrategy{
    @Override
    public boolean isMatch(char c) {
        return c != '\n' && c != '\r';
    }
}
