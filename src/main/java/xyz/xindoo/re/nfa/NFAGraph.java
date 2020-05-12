package xyz.xindoo.re.nfa;

import xyz.xindoo.re.common.Constant;

public class NFAGraph {
    public NFAState start;
    public NFAState end;
    public NFAGraph(NFAState start, NFAState end) {
        this.start = start;
        this.end = end;
    }

    // |
    public void addParallelGraph(NFAGraph NFAGraph) {
        NFAState newStart = new NFAState();
        NFAState newEnd = new NFAState();
        newStart.addNext(Constant.EPSILON, this.start);
        newStart.addNext(Constant.EPSILON, NFAGraph.start);
        this.end.addNext(Constant.EPSILON, newEnd);
        NFAGraph.end.addNext(Constant.EPSILON, newEnd);
        this.start = newStart;
        this.end = newEnd;
    }

    //
    public void addSeriesGraph(NFAGraph NFAGraph) {
        this.end.addNext(Constant.EPSILON, NFAGraph.start);
        this.end = NFAGraph.end;
    }

    // * 重复0-n次
    public void repeatStar() {
        repeatPlus();
        addSToE(); // 重复0
    }

    // ? 重复0次哦
    public void addSToE() {
        start.addNext(Constant.EPSILON, end);
    }

    // + 重复1-n次
    public void repeatPlus() {
        NFAState newStart = new NFAState();
        NFAState newEnd = new NFAState();
        newStart.addNext(Constant.EPSILON, this.start);
        end.addNext(Constant.EPSILON, newEnd);
        end.addNext(Constant.EPSILON, start);
        this.start = newStart;
        this.end = newEnd;
    }

}
