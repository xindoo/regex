package xyz.xindoo.re.dfa;

import xyz.xindoo.re.common.State;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DFAGraph {

    private Map<String, DFAState> map = new HashMap<>();
    public DFAState start = new DFAState();

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
        if (!map.containsKey(allStateIds)) {
            DFAState dfaState = new DFAState(allStateIds, states);
            map.put(allStateIds, dfaState);
        }
        return map.get(allStateIds);
    }
}
