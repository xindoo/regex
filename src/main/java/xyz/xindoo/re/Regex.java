package xyz.xindoo.re;

import xyz.xindoo.re.common.Constant;
import xyz.xindoo.re.common.Reader;
import xyz.xindoo.re.common.State;
import xyz.xindoo.re.dfa.DFAGraph;
import xyz.xindoo.re.dfa.DFAState;
import xyz.xindoo.re.nfa.NFAGraph;
import xyz.xindoo.re.nfa.NFAState;
import xyz.xindoo.re.nfa.strategy.MatchStrategy;
import xyz.xindoo.re.nfa.strategy.MatchStrategyManager;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class Regex {
    private NFAGraph nfaGraph;
    private DFAGraph dfaGraph;

    public static Regex compile(String regex) throws Exception {
        if (regex == null || regex.length() == 0) {
            throw new Exception("regex cannot be empty!");
        }
        NFAGraph nfaGraph = regex2nfa(regex);
        nfaGraph.end.setStateType();
        DFAGraph dfaGraph = convertNfa2Dfa(nfaGraph);
        return new Regex(nfaGraph, dfaGraph);
    }

    private Regex(NFAGraph nfaGraph, DFAGraph dfaGraph) {
        this.nfaGraph = nfaGraph;
        this.dfaGraph = dfaGraph;
    }

    public void printNfa() {
        Queue<State> queue = new ArrayDeque<>();
        Set<Integer> addedStates = new HashSet<>();
        queue.add(nfaGraph.start);
        addedStates.add(nfaGraph.start.getId());
        while (!queue.isEmpty()) {
            State curState = queue.poll();
            for (Map.Entry<String, Set<State>> entry : curState.next.entrySet()) {
                String key = entry.getKey();
                Set<State> nexts = entry.getValue();
                for (State next : nexts) {
                    System.out.printf("%2d->%2d  %s\n", curState.getId(), next.getId(), key);
                    if (!addedStates.contains(next.getId())) {
                        queue.add(next);
                        addedStates.add(next.getId());
                    }
                }
            }
        }
    }

    public void printDfa() {
        Queue<State> queue = new ArrayDeque<>();
        Set<String> addedStates = new HashSet<>();
        queue.add(dfaGraph.start);
        addedStates.add(((DFAState)dfaGraph.start).getAllStateIds());
        while (!queue.isEmpty()) {
            State curState = queue.poll();

            for (Map.Entry<String, Set<State>> entry : curState.next.entrySet()) {
                String key = entry.getKey();
                Set<State> nexts = entry.getValue();
                for (State next : nexts) {
                    System.out.printf("%s -> %s  %s \n", ((DFAState)curState).getAllStateIds(),((DFAState)next).getAllStateIds(),  key);
                    if (!addedStates.contains(((DFAState)next).getAllStateIds())) {
                        queue.add(next);
                        addedStates.add(((DFAState)next).getAllStateIds());
                    }
                }
            }
        }
    }

    private static NFAGraph regex2nfa(String regex) {
        Reader reader = new Reader(regex);
        NFAGraph nfaGraph = null;
        while (reader.hasNext()) {
            char ch = reader.next();
            String edge = null;
            switch (ch) {
                // 子表达式特殊处理
                case '(' : {
                    String subRegex = reader.getSubRegex(reader);
                    NFAGraph newNFAGraph = regex2nfa(subRegex);
                    checkRepeat(reader, newNFAGraph);
                    if (nfaGraph == null) {
                        nfaGraph = newNFAGraph;
                    } else {
                        nfaGraph.addSeriesGraph(newNFAGraph);
                    }
                    break;
                }
                // 或表达式特殊处理
                case '|' : {
                    String remainRegex = reader.getRemainRegex(reader);
                    NFAGraph newNFAGraph = regex2nfa(remainRegex);
                    if (nfaGraph == null) {
                        nfaGraph = newNFAGraph;
                    } else {
                        nfaGraph.addParallelGraph(newNFAGraph);
                    }
                    break;
                }
                case '[' : {
                    edge = getCharSetMatch(reader);
                    break;
                }
                case '^' : {
                    break;
                }
                case '$' : {
                    break;
                }
                case '.' : {
                    edge = ".";
                    break;
                }
                // 处理特殊占位符
                case '\\' : {
                    char nextCh = reader.next();
                    switch (nextCh) {
                        case 'd': {
                            edge = "\\d";
                            break;
                        }
                        case 'D': {
                            edge = "\\D";
                            break;
                        }
                        case 'w': {
                            edge = "\\w";
                            break;
                        }
                        case 'W': {
                            edge = "\\W";
                            break;
                        }
                        case 's': {
                            edge = "\\s";
                            break;
                        }
                        case 'S': {
                            edge = "\\S";
                            break;
                        }
                        // 转义后的字符匹配
                        default:{
                            edge = String.valueOf(nextCh);
                            break;
                        }
                    }
                    break;
                }

                default : {  // 处理普通字符
                    edge = String.valueOf(ch);
                    break;
                }
            }
            if (edge != null) {
                NFAState start = new NFAState();
                NFAState end = new NFAState();
                start.addNext(edge, end);
                NFAGraph newNFAGraph = new NFAGraph(start, end);
                checkRepeat(reader, newNFAGraph);
                if (nfaGraph == null) {
                    nfaGraph = newNFAGraph;
                } else {
                    nfaGraph.addSeriesGraph(newNFAGraph);
                }
            }
        }
        return nfaGraph;
    }

    private static DFAGraph convertNfa2Dfa(NFAGraph nfaGraph) {
        DFAGraph dfaGraph = new DFAGraph();
        Set<State> startStates = new HashSet<>();
        startStates.addAll(getNextEStates(nfaGraph.start, new HashSet<>()));
        if (startStates.size() == 0) {
            startStates.add(nfaGraph.start);
        }
        dfaGraph.start = dfaGraph.getOrBuild(startStates);
        Queue<DFAState> queue = new LinkedList<>();
        Set<State> finishedStates = new HashSet<>();
        queue.add(dfaGraph.start);
        while (!queue.isEmpty()) {
            DFAState curState = queue.poll();
            for (State nfaState : curState.nfaStates) {
                Set<State> nextStates = new HashSet<>();
                Set<String> finishedEdges = new HashSet<>();
                finishedEdges.add(Constant.EPSILON);
                for (String edge : nfaState.next.keySet()) {
                    if (finishedEdges.contains(edge)) {
                        continue;
                    }
                    finishedEdges.add(edge);
                    Set<State> efinishedState = new HashSet<>();
                    for (State state : curState.nfaStates) {
                        Set<State> edgeStates = state.next.getOrDefault(edge, Collections.emptySet());
                        nextStates.addAll(edgeStates);
                        for (State eState : edgeStates) {  // 添加E可达节点
                            if (efinishedState.contains(eState)) {
                                continue;
                            }
                            nextStates.addAll(getNextEStates(eState, efinishedState));
                            efinishedState.add(eState);
                        }
                    }
                    DFAState nextDFAstate = dfaGraph.getOrBuild(nextStates);
                    if (!finishedStates.contains(nextDFAstate)) {
                        queue.add(nextDFAstate);
                    }
                    curState.addNext(edge, nextDFAstate);
                }
            }
            finishedStates.add(curState);
        }
        return dfaGraph;
    }

    private static void checkRepeat(Reader reader, NFAGraph newNFAGraph) {
        char nextCh = reader.peak();
        switch (nextCh) {
            case '*': {
                newNFAGraph.repeatStar();
                reader.next();
                break;
            } case '+': {
                newNFAGraph.repeatPlus();
                reader.next();
                break;
            } case '?' : {
                newNFAGraph.addSToE();
                reader.next();
                break;
            } case '{' : {
                //
                break;
            }  default : {
                return;
            }
        }
    }

    public boolean isMatch(String text) {
        return isMatch(text, 0);
    }

    public boolean isMatch(String text, int mode) {
        State start = nfaGraph.start;
        if (mode == 1) {
            start = dfaGraph.start;
        }
        return isMatch(text, 0, start);
    }

    private boolean isMatch(String text, int pos, State curNFAState) {
        if (pos == text.length()) {
            if (curNFAState.isEndState()) {
                return true;
            }
            for (State nextState : curNFAState.next.getOrDefault(Constant.EPSILON, Collections.emptySet())) {
                if (isMatch(text, pos, nextState)) {
                    return true;
                }
            }
            return false;
        }

        for (Map.Entry<String, Set<State>> entry : curNFAState.next.entrySet()) {
            String edge = entry.getKey();
            if (Constant.EPSILON.equals(edge)) {
                for (State nextState : entry.getValue()) {
                    if (isMatch(text, pos, nextState)) {
                        return true;
                    }
                }
            } else {
                MatchStrategy matchStrategy = MatchStrategyManager.getStrategy(edge);
                if (!matchStrategy.isMatch(text.charAt(pos), edge)) {
                    continue;
                }
                // 遍历匹配策略
                for (State nextState : entry.getValue()) {
                    if (isMatch(text, pos + 1, nextState)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 暂时只支持字母 数字
     * */
    private static String getCharSetMatch(Reader reader) {
        String charSet = "";
        char ch;
        while ((ch = reader.next()) != ']') {
            charSet += ch;
        }
        return charSet;
    }

    private static int[] getRange(Reader reader) {
        String rangeStr = "";
        char ch;
        while ((ch = reader.next()) != '}') {
            if (ch == ' ') {
                continue;
            }
            rangeStr += ch;
        }
        int[] res = new int[2];
        if (!rangeStr.contains(",")) {
            res[0] = Integer.parseInt(rangeStr);
            res[1] = res[0];
        } else {
            String[] se = rangeStr.split(",", -1);
            res[0] = Integer.parseInt(se[0]);
            if (se[1].length() == 0) {
                res[1] = Integer.MAX_VALUE;
            } else {
                res[1] = Integer.parseInt(se[1]);
            }
        }
        return res;
    }

    private static Set<State> getNextEStates(State curState, Set<State> stateSet) {
        if (!curState.next.containsKey(Constant.EPSILON)) {
            return Collections.emptySet();
        }
        Set<State> res = new HashSet<>();
        for (State state : curState.next.get(Constant.EPSILON)) {
            if (stateSet.contains(state)) {
                continue;
            }
            res.add(state);
            res.addAll(getNextEStates(state, stateSet));
            stateSet.add(state);
        }
        return res;
    }
}
