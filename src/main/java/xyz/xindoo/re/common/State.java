package xyz.xindoo.re.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class State {
    protected static int idCnt = 0;
    protected int id;
    protected int stateType;

    public State() {
        this.id = idCnt++;
    }

    public Map<String, List<State>> next = new HashMap<>();

    public void addNext(String edge, State nfaState) {
        List<State> list = next.get(edge);
        if (list == null) {
            list = new ArrayList<>();
            next.put(edge, list);
        }
        list.add(nfaState);
    }

    public void setStateType() {
        stateType = 1;
    }

    public boolean isEndState() {
        return stateType == 1;
    }

    public int getId() {
        return this.id;
    }
}
