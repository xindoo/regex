package xyz.xindoo.re;

import xyz.xindoo.re.strategy.CharMatchStrategy;
import xyz.xindoo.re.strategy.DigitalMatchStrategy;
import xyz.xindoo.re.strategy.DotMatchStrategy;
import xyz.xindoo.re.strategy.EpsilonMatchStrategy;
import xyz.xindoo.re.strategy.MatchStrategy;
import xyz.xindoo.re.strategy.SpaceMatchStrategy;
import xyz.xindoo.re.strategy.WMatchStrategy;

import java.util.List;
import java.util.Map;

public class Regex {
    private Graph nfaGraph;
    public static Regex compile(String regex) throws Exception {
        if (regex == null || regex.length() == 0) {
            throw new Exception("regex cannot be empty!");
        }
        Graph graph = regex2nfa(regex);
        graph.end.setStateType();
        return new Regex(graph);
    }

    private Regex(Graph graph) {
        this.nfaGraph = graph;
    }

    private static Graph regex2nfa(String regex) {
        Reader reader = new Reader(regex);
        Graph graph = null;
        while (reader.hasNext()) {
            char ch = reader.next();
            MatchStrategy matchStrategy = null;
            switch (ch) {
                // 子表达式特殊处理
                case '(' : {
                    String subRegex = reader.getSubRegex(reader);
                    Graph newGraph = regex2nfa(subRegex);
                    checkRepeat(reader, newGraph);
                    if (graph == null) {
                        graph = newGraph;
                    } else {
                        graph.addSeriesGraph(newGraph);
                    }
                    break;
                }
                // 或表达式特殊处理
                case '|' : {
                    String remainRegex = reader.getRemainRegex(reader);
                    Graph newGraph = regex2nfa(remainRegex);
                    if (graph == null) {
                        graph = newGraph;
                    } else {
                        graph.addParallelGraph(newGraph);
                    }
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
                        // 以下是特殊符号
                        case '\\' :
                        case '.' :
                        case '?' :
                        case '+' :
                        case '*' :
                        case '{' :
                        case '}' :
                        case '[' :
                        case ']' :
                        case '(' :
                        case ')' : {
                            matchStrategy = new CharMatchStrategy(nextCh);
                            break;
                        }
                        default:{
                            System.out.println("error");
                        }
                    }
                    break;
                }

                default : {  // 处理普通字符
                    matchStrategy = new CharMatchStrategy(ch);
                }
            }

            // 表明有某类字符的匹配
            if (matchStrategy != null) {
                State start = new State();
                State end = new State();
                start.addNext(matchStrategy, end);
                Graph newGraph = new Graph(start, end);
                checkRepeat(reader, newGraph);
                if (graph == null) {
                    graph = newGraph;
                } else {
                    graph.addSeriesGraph(newGraph);
                }
            }
        }
        return graph;
    }

    private static void checkRepeat(Reader reader, Graph newGraph) {
        char nextCh = reader.peak();
        switch (nextCh) {
            case '*': {
                newGraph.repeatStar();
                reader.next();
                break;
            } case '+': {
                newGraph.repeatPlus();
                reader.next();
                break;
            } case '?' : {
                newGraph.addSToE();
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
                // 遍历匹配策略
                if (!matchStrategy.isMatch(text.charAt(pos))) {
                    continue;
                }
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
}
