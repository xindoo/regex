package xyz.xindoo.re.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class State {
    protected static int idCnt = 0;
    protected int id;
    protected int stateType;

    public State() {
        this.id = idCnt++;
    }

    public Map<String, Set<State>> next = new HashMap<>();

    public void addNext(String edge, State nfaState) {
        Set<State> set = next.get(edge);
        if (set == null) {
            set = new HashSet<>();
            next.put(edge, set);
        }
        set.add(nfaState);
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
