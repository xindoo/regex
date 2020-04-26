package xyz.xindoo.re.strategy;

public class DotMatchStrategy implements MatchStrategy{
    @Override
    public boolean isMatch(char c) {
        return c != '\n' && c != '\r';
    }
}
