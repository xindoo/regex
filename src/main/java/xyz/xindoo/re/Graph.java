package xyz.xindoo.re;

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
        newStart.addNext(EPSILON, this.start);
        newStart.addNext(EPSILON, graph.start);
        this.end.addNext(EPSILON, newEnd);
        graph.end.addNext(EPSILON, newEnd);
        this.start = newStart;
        this.end = newEnd;
    }

    //
    public void addSeriesGraph(Graph graph) {
        this.end.addNext(EPSILON, graph.start);
        this.end = graph.end;
    }

    // * 重复0-n次
    public void repeatStar() {
        repeatPlus();
        addSToE(); // 重复0
    }

    // ? 重复0次哦
    public void addSToE() {
        start.addNext(EPSILON, end);
    }

    // + 重复1-n次
    public void repeatPlus() {
        State newStart = new State();
        State newEnd = new State();
        newStart.addNext(EPSILON, this.start);
        end.addNext(EPSILON, newEnd);
        end.addNext(EPSILON, start);
        this.start = newStart;
        this.end = newEnd;
    }

}
