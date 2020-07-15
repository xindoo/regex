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
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class Regex {
    private NFAGraph nfaGraph;
    private DFAGraph dfaGraph;

    public static Regex compile(String regex) {
        if (regex == null || regex.length() == 0) {
            return null;
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

    /**
     * 有向图的广度优先遍历
     */
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
        addedStates.add(dfaGraph.start.getAllStateIds());
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
                // 暂时未支持零宽断言
                case '^' : {
                    break;
                }
                // 暂未支持
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

    /**
     * 使用子集构造法把nfa转成dfa,具体可以参考博客 https://blog.csdn.net/xindoo/article/details/106458165
     */
    private static DFAGraph convertNfa2Dfa(NFAGraph nfaGraph) {
        DFAGraph dfaGraph = new DFAGraph();
        Set<State> startStates = new HashSet<>();
        // 用NFA图的起始节点构造DFA的起始节点
        startStates.addAll(getNextEStates(nfaGraph.start, new HashSet<>()));
        if (startStates.size() == 0) {
            startStates.add(nfaGraph.start);
        }
        dfaGraph.start = dfaGraph.getOrBuild(startStates);
        Queue<DFAState> queue = new LinkedList<>();
        Set<State> finishedStates = new HashSet<>();
        // 如果BFS的方式从已找到的起始节点遍历并构建DFA
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
                        for (State eState : edgeStates) {
                            // 添加E可达节点
                            if (efinishedState.contains(eState)) {
                                continue;
                            }
                            nextStates.addAll(getNextEStates(eState, efinishedState));
                            efinishedState.add(eState);
                        }
                    }
                    // 将NFA节点列表转化为DFA节点，如果已经有对应的DFA节点就返回，否则创建一个新的DFA节点
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
                // 暂未支持{}指定重复次数
                break;
            }  default : {
                return;
            }
        }
    }

    /**
     * 获取[]中表示的字符集,只支持字母 数字
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

    // 获取Epsilon可达节点列表
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

    /**
     * 匹配过程就是根据输入遍历图的过程, 这里DFA和NFA用了同样的代码, 但实际上因为DFA的特性是不会产生回溯的,
     * 所以DFA可以换成非递归的形式
     */
    private boolean isMatch(String text, int pos, State curState) {
        if (pos == text.length()) {
            for (State nextState : curState.next.getOrDefault(Constant.EPSILON, Collections.emptySet())) {
                if (isMatch(text, pos, nextState)) {
                    return true;
                }
            }
            if (curState.isEndState()) {
                return true;
            }
            return false;
        }

        for (Map.Entry<String, Set<State>> entry : curState.next.entrySet()) {
            String edge = entry.getKey();
            // 这个if和else的先后顺序决定了是贪婪匹配还是非贪婪匹配
            if (Constant.EPSILON.equals(edge)) {
                // 如果是DFA模式,不会有EPSILON边,所以不会进这
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
                    // 如果是DFA匹配模式,entry.getValue()虽然是set,但里面只会有一个元素,所以不需要回溯
                    if (nextState instanceof DFAState) {
                        return isMatch(text, pos + 1, nextState);
                    }
                    if (isMatch(text, pos + 1, nextState)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isDfaMatch(String text) {
        return isDfaMatch(text, 0, dfaGraph.start);
    }

    private boolean isDfaMatch(String text, int pos, State startState) {
        State curState = startState;
        while (pos < text.length()) {
            boolean canContinue = false;
            for (Map.Entry<String, Set<State>> entry : curState.next.entrySet()) {
                String edge = entry.getKey();
                MatchStrategy matchStrategy = MatchStrategyManager.getStrategy(edge);
                if (matchStrategy.isMatch(text.charAt(pos), edge)) {
                    curState = entry.getValue().stream().findFirst().orElse(null);
                    pos++;
                    canContinue = true;
                    break;
                }
            }
            if (!canContinue) {
                return false;
            }
        }
        return curState.isEndState();
    }

    public List<String> match(String text) {
        return match(text, 0);
    }

    public List<String> match(String text, int mod) {
        int s = 0;
        int e = -1;
        List<String> res = new LinkedList<>();
        while (s != text.length()) {
            e = getMatchEnd(text, s, dfaGraph.start);
            if (e != -1) {
                res.add(text.substring(s, e));
                s = e;
            } else {
                s++;
            }
        }
        return res;
    }

    // 获取正则表达式在字符串中能匹配到的结尾的位置
    private int getMatchEnd(String text, int pos, State curState) {
        int end = -1;
        if (curState.isEndState()) {
            return pos;
        }

        if (pos == text.length()) {
            for (State nextState : curState.next.getOrDefault(Constant.EPSILON, Collections.emptySet())) {
                end = getMatchEnd(text, pos, nextState);
                if (end != -1) {
                    return end;
                }
            }
        }

        for (Map.Entry<String, Set<State>> entry : curState.next.entrySet()) {
            String edge = entry.getKey();
            if (Constant.EPSILON.equals(edge)) {
                for (State nextState : entry.getValue()) {
                    end = getMatchEnd(text, pos, nextState);
                    if (end != -1) {
                        return end;
                    }
                }
            } else {
                MatchStrategy matchStrategy = MatchStrategyManager.getStrategy(edge);
                if (!matchStrategy.isMatch(text.charAt(pos), edge)) {
                    continue;
                }
                // 遍历匹配策略
                for (State nextState : entry.getValue()) {
                    end = getMatchEnd(text, pos + 1, nextState);
                    if (end != -1) {
                        return end;
                    }
                }
            }
        }
        return -1;
    }
}
