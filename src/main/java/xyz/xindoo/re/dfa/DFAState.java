package xyz.xindoo.re.dfa;

import xyz.xindoo.re.common.State;

import java.util.HashSet;
import java.util.Set;

public class DFAState extends State {
    public Set<State> nfaStates = new HashSet<>();
    private String allStateIds;
    public DFAState() {
        this.stateType = 2;
    }

    public DFAState(String allStateIds, Set<State> states) {
        this.allStateIds = allStateIds;
        this.nfaStates.addAll(states);
        for (State state : states) {
            if (state.isEndState()) {
                this.stateType = 1;
            }
        }
    }
}
