package xyz.xindoo.re.nfa.strategy;

public class CharMatchStrategy extends MatchStrategy{
    private char cur;
    public CharMatchStrategy(char c) {
        this.cur = c;
    }

    private CharMatchStrategy() {

    }

    @Override
    public boolean isMatch(char c) {
        return cur == c;
    }
}
