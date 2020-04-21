package xyz.xindoo.re;

import java.util.Collections;

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
            switch (ch) {
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
                case '|' : {  // 对or的支持目前有些优先级问题
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
                    Graph newGraph = null;
                    break;
                }
                case '$' : {
                    break;
                }
                // 处理特殊占位符
                case '\\' : {
                    break;
                }
                default : {  // 处理普通字符
                    State start = new State();
                    State end = new State();
                    start.addNext(String.valueOf(ch), end);
                    Graph newGraph = new Graph(start, end);
                    checkRepeat(reader, newGraph);
                    if (graph == null) {
                        graph = newGraph;
                    } else {
                        graph.addSeriesGraph(newGraph);
                    }
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
        if (pos == text.length() && curState.isEndState()) {
            return true;
        }

        for (State nextState : curState.next.getOrDefault(Graph.EPSILON, Collections.emptyList())) {
            if (isMatch(text, pos, nextState)) {
                return true;
            }
        }

        for (State nextState : curState.next.getOrDefault(Graph.DOT, Collections.emptyList())) {
            if (isMatch(text, pos + 1, nextState)) {
                return true;
            }
        }

        // 字符串遍历完了,但仍旧没有到终止态
        if (pos == text.length()) {
            return false;
        }
        String path = String.valueOf(text.charAt(pos));
        for (State nextState : curState.next.getOrDefault(path, Collections.emptyList())) {
            if (isMatch(text, pos+1, nextState)) {
                return true;
            }
        }
        return false;
    }
}
