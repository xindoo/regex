package xyz.xindoo.re;

import xyz.xindoo.re.nfa.strategy.EpsilonMatchStrategy;
import xyz.xindoo.re.nfa.NFAGraph;
import xyz.xindoo.re.nfa.strategy.MatchStrategy;
import xyz.xindoo.re.nfa.strategy.MatchStrategyManager;

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
            String edge = null;
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

            State start = new State();
            State end = new State();
            start.addNext(edge, end);
            NFAGraph newNFAGraph = new NFAGraph(start, end);
            checkRepeat(reader, newNFAGraph);
            if (NFAGraph == null) {
                NFAGraph = newNFAGraph;
            } else {
                NFAGraph.addSeriesGraph(newNFAGraph);
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

        for (Map.Entry<String, List<State>> entry : curState.next.entrySet()) {
            String edge = entry.getKey();
            MatchStrategy matchStrategy = MatchStrategyManager.getStrategy(edge);
            if (matchStrategy instanceof EpsilonMatchStrategy) {
                for (State nextState : entry.getValue()) {
                    if (isMatch(text, pos, nextState)) {
                        return true;
                    }
                }
            } else {
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
    static String getCharSetMatch(Reader reader) {
        String charSet = "";
        char ch;
        while ((ch = reader.next()) != ']') {
            charSet += ch;
        }
        return charSet;
    }
}
