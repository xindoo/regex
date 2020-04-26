package xyz.xindoo.re.strategy;

public class CharSetMatchStrategy implements MatchStrategy{
    @Override
    public boolean isMatch(char c) {
        return c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z';
    }
}
