package xyz.xindoo.re;

import xyz.xindoo.re.nfa.strategy.MatchStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class State {
    private static int idCnt = 0;
    private int id;
    private int stateType;

    public State() {
        this.id = idCnt++;
    }

    Map<String, List<State>> next = new HashMap<>();

    public void addNext(String edge, State state) {
        List<State> list = next.get(edge);
        if (list == null) {
            list = new ArrayList<>();
            next.put(edge, list);
        }
        list.add(state);
    }
    protected void setStateType() {
        stateType = 1;
    }
    protected boolean isEndState() {
        return stateType == 1;
    }
}
