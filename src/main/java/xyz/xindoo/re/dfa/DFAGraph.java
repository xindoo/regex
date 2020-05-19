package xyz.xindoo.re.dfa;

import xyz.xindoo.re.common.State;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DFAGraph {

    private Map<String, DFAState> nfaStates2dfaState = new HashMap<>();
    public DFAState start = new DFAState();

    // 这里用map保存NFAState结合是已有对应的DFAState, 有就直接拿出来用
    public DFAState getOrBuild(Set<State> states) {
        String allStateIds = "";
        int[] ids = states.stream()
                          .mapToInt(state -> state.getId())
                          .sorted()
                          .toArray();
        for (int id : ids) {
            allStateIds += "#";
            allStateIds += id;
        }
        if (!nfaStates2dfaState.containsKey(allStateIds)) {
            DFAState dfaState = new DFAState(allStateIds, states);
            nfaStates2dfaState.put(allStateIds, dfaState);
        }
        return nfaStates2dfaState.get(allStateIds);
    }
}
