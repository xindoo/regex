package xyz.xindoo.re.strategy;

public class CharMatchStrategy implements MatchStrategy{
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
