package xyz.xindoo.re;

import xyz.xindoo.re.strategy.EpsilonMatchStrategy;
import xyz.xindoo.re.strategy.MatchStrategy;

public class Graph {
    public static String EPSILON = "epsilon";
    public static String DOT = ".";

    public State start;
    public State end;
    public Graph(State start, State end) {
        this.start = start;
        this.end = end;
    }

    // |
    public void addParallelGraph(Graph graph) {
        State newStart = new State();
        State newEnd = new State();
        MatchStrategy path = new EpsilonMatchStrategy();
        newStart.addNext(path, this.start);
        newStart.addNext(path, graph.start);
        this.end.addNext(path, newEnd);
        graph.end.addNext(path, newEnd);
        this.start = newStart;
        this.end = newEnd;
    }

    //
    public void addSeriesGraph(Graph graph) {
        MatchStrategy path = new EpsilonMatchStrategy();
        this.end.addNext(path, graph.start);
        this.end = graph.end;
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
