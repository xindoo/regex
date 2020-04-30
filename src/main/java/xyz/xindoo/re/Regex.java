package xyz.xindoo.re;

import xyz.xindoo.re.nfa.strategy.CharMatchStrategy;
import xyz.xindoo.re.nfa.strategy.CharSetMatchStrategy;
import xyz.xindoo.re.nfa.strategy.DigitalMatchStrategy;
import xyz.xindoo.re.nfa.strategy.DotMatchStrategy;
import xyz.xindoo.re.nfa.strategy.EpsilonMatchStrategy;
import xyz.xindoo.re.nfa.NFAGraph;
import xyz.xindoo.re.nfa.strategy.MatchStrategy;
import xyz.xindoo.re.nfa.strategy.SpaceMatchStrategy;
import xyz.xindoo.re.nfa.strategy.WMatchStrategy;

import java.util.List;
import java.util.Map;

public class Regex {
    private NFAGraph nfaGraph;
    public static Regex compile(String regex) throws Exception {
        if (regex == null || regex.length() == 0) {
            throw new Exception("regex cannot be empty!");
        }
        NFAGraph NFAGraph = regex2nfa(regex);
        NFAGraph.end.setStateType();
        return new Regex(NFAGraph);
    }

    private Regex(NFAGraph NFAGraph) {
        this.nfaGraph = NFAGraph;
    }

    private static NFAGraph regex2nfa(String regex) {
        Reader reader = new Reader(regex);
        NFAGraph NFAGraph = null;
        while (reader.hasNext()) {
            char ch = reader.next();
            MatchStrategy matchStrategy = null;
            switch (ch) {
                // 子表达式特殊处理
                case '(' : {
                    String subRegex = reader.getSubRegex(reader);
                    NFAGraph newNFAGraph = regex2nfa(subRegex);
                    checkRepeat(reader, newNFAGraph);
                    if (NFAGraph == null) {
                        NFAGraph = newNFAGraph;
                    } else {
                        NFAGraph.addSeriesGraph(newNFAGraph);
                    }
                    break;
                }
                // 或表达式特殊处理
                case '|' : {
                    String remainRegex = reader.getRemainRegex(reader);
                    NFAGraph newNFAGraph = regex2nfa(remainRegex);
                    if (NFAGraph == null) {
                        NFAGraph = newNFAGraph;
                    } else {
                        NFAGraph.addParallelGraph(newNFAGraph);
                    }
                    break;
                }
                case '[' : {
                    matchStrategy = getCharSetMatch(reader);
                    break;
                }
                case '^' : {
                    break;
                }
                case '$' : {
                    break;
                }
                case '.' : {
                    matchStrategy = new DotMatchStrategy();
                    break;
                }
                // 处理特殊占位符
                case '\\' : {
                    char nextCh = reader.next();
                    switch (nextCh) {
                        case 'd': {
                            matchStrategy = new DigitalMatchStrategy(false);
                            break;
                        }
                        case 'D': {
                            matchStrategy = new DigitalMatchStrategy(true);
                            break;
                        }
                        case 'w': {
                            matchStrategy = new WMatchStrategy(false);
                            break;
                        }
                        case 'W': {
                            matchStrategy = new WMatchStrategy(true);
                            break;
                        }
                        case 's': {
                            matchStrategy = new SpaceMatchStrategy(false);
                            break;
                        }
                        case 'S': {
                            matchStrategy = new SpaceMatchStrategy(true);
                            break;
                        }
                        // 转义后的字符匹配
                        default:{
                            matchStrategy = new CharMatchStrategy(nextCh);
                            break;
                        }
                    }
                    break;
                }

                default : {  // 处理普通字符
                    matchStrategy = new CharMatchStrategy(ch);
                    break;
                }
            }

            // 表明有某类单字符的匹配
            if (matchStrategy != null) {
                State start = new State();
                State end = new State();
                start.addNext(matchStrategy, end);
                NFAGraph newNFAGraph = new NFAGraph(start, end);
                checkRepeat(reader, newNFAGraph);
                if (NFAGraph == null) {
                    NFAGraph = newNFAGraph;
                } else {
                    NFAGraph.addSeriesGraph(newNFAGraph);
                }
            }
        }
        return NFAGraph;
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
        return isMatch(text, 0, nfaGraph.start);
    }

    private boolean isMatch(String text, int pos, State curState) {
        if (pos == text.length()) {
            if (curState.isEndState()) {
                return true;
            }
            return false;
        }

        for (Map.Entry<MatchStrategy, List<State>> entry : curState.next.entrySet()) {
            MatchStrategy matchStrategy = entry.getKey();
            if (matchStrategy instanceof EpsilonMatchStrategy) {
                for (State nextState : entry.getValue()) {
                    if (isMatch(text, pos, nextState)) {
                        return true;
                    }
                }
            } else {
                if (!matchStrategy.isMatch(text.charAt(pos))) {
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

//    private int getMatchEnd(String text, int pos, State curState) {
//        if (curState.isEndState()) {
//            return pos;
//        }
//
//        for (State nextState : curState.next.getOrDefault(Graph.EPSILON, Collections.emptyList())) {
//            int end = getMatchEnd(text, pos, nextState);
//            if (end != -1) {
//                return end;
//            }
//        }
//
//        for (State nextState : curState.next.getOrDefault(Graph.DOT, Collections.emptyList())) {
//            int end = getMatchEnd(text, pos + 1, nextState);
//            if (end != -1) {
//                return end;
//            }
//        }
//
//        // 字符串遍历完了,但仍旧没有到终止态
//        if (pos == text.length()) {
//            return -1;
//        }
//
//        String path = String.valueOf(text.charAt(pos));
//        for (State nextState : curState.next.getOrDefault(path, Collections.emptyList())) {
//            int end = getMatchEnd(text, pos + 1, nextState);
//            if (end != -1) {
//                return end;
//            }
//        }
//        return -1;
//    }
//
//    public List<String> match(String text) {
//        List<String> res = new ArrayList<>();
//        for (int i = 0; i < text.length(); i++) {
//            int end = getMatchEnd(text, i, nfaGraph.start);
//            if (end != -1) {
//                res.add(text.substring(i, end));
//                i = end - 1;
//            }
//        }
//        return res;
//    }
    /**
     * 暂时只支持字母 数字
     * */
    static CharSetMatchStrategy getCharSetMatch(Reader reader) {
        String charSet = "";
        boolean isReverse = false;
        char ch;
        while ((ch = reader.next()) != ']') {
            if (ch == '^') {
                isReverse = true;
                continue;
            }
            charSet += ch;
        }
        return new CharSetMatchStrategy(charSet, isReverse);
    }
}
