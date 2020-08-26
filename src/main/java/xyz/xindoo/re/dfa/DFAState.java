package xyz.xindoo.re.dfa;

import xyz.xindoo.re.common.State;
import xyz.xindoo.re.common.StateType;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class DFAState extends State {
    public Set<State> nfaStates = new HashSet<>();
    // 保存对应NFAState的id,一个DFAState可能是多个NFAState的集合,所以拼接成String
    private String allStateIds;
    public DFAState() {
        this.stateType = StateType.GENERAL;
    }

    public DFAState(String allStateIds, Set<State> states) {
        this.allStateIds = allStateIds;
        this.nfaStates.addAll(states);

        for (State state : states) {
            if (state.isEndState()) {  // 如果有任意节点是终止态,新建的DFA节点就是终止态
                this.stateType = StateType.END;
            }
        }
    }

    public String getAllStateIds() {
        return allStateIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return allStateIds.equals(((DFAState)o).allStateIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(allStateIds);
    }
}
