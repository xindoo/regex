package xyz.xindoo.re.nfa;

import xyz.xindoo.re.State;
import xyz.xindoo.re.nfa.strategy.EpsilonMatchStrategy;
import xyz.xindoo.re.nfa.strategy.MatchStrategy;

public class NFAGraph {
    public static String EPSILON = "epsilon";
    public static String DOT = ".";

    public State start;
    public State end;
    public NFAGraph(State start, State end) {
        this.start = start;
        this.end = end;
    }

    // |
    public void addParallelGraph(NFAGraph NFAGraph) {
        State newStart = new State();
        State newEnd = new State();
        MatchStrategy path = new EpsilonMatchStrategy();
        newStart.addNext(path, this.start);
        newStart.addNext(path, NFAGraph.start);
        this.end.addNext(path, newEnd);
        NFAGraph.end.addNext(path, newEnd);
        this.start = newStart;
        this.end = newEnd;
    }

    //
    public void addSeriesGraph(NFAGraph NFAGraph) {
        MatchStrategy path = new EpsilonMatchStrategy();
        this.end.addNext(path, NFAGraph.start);
        this.end = NFAGraph.end;
    }

    // * 重复0-n次
    public void repeatStar() {
        repeatPlus();
        addSToE(); // 重复0
    }

    // ? 重复0次哦
    public void addSToE() {
        MatchStrategy path = new EpsilonMatchStrategy();
        start.addNext(path, end);
    }

    // + 重复1-n次
    public void repeatPlus() {
        State newStart = new State();
        State newEnd = new State();
        MatchStrategy path = new EpsilonMatchStrategy();
        newStart.addNext(path, this.start);
        end.addNext(path, newEnd);
        end.addNext(path, start);
        this.start = newStart;
        this.end = newEnd;
    }

}
