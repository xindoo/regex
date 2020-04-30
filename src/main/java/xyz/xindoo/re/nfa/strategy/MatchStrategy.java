package xyz.xindoo.re.nfa.strategy;

public class MatchStrategy {
    protected boolean isReverse = false;
    protected String metaData = "";

    public void setReverse(boolean reverse) {
        isReverse = reverse;
    }

    public boolean isMatch(char c){
        return false;
    }

    public boolean equals(MatchStrategy other) {
        return this.metaData.equals(other.metaData);
    }
}
