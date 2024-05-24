package com.attackonarchitect.matcher;

import com.attackonarchitect.utils.AssertUtil;
import com.attackonarchitect.utils.Tuple;

import java.util.*;

/**
 * 匹配的结点
 *
 * @author <a href="mailto:675464934@qq.com">Terrdi</a>
 * @date 2024/5/16
 * @since 1.8
 **/
class MatchNode {
    /**
     * 下一个字符的集合
     */
    private final Map<Character, MatchNode> nextNode = new HashMap<>();

    private final char currValue;

    private Object value;

    static final char END_EOF = '\0';

    MatchNode(char currValue, Object value) {
        this.currValue = currValue;
        this.value = value;
    }

    MatchNode(char currValue) {
        this(currValue, END_EOF);
    }

    public boolean contains(final char next) {
        return this.nextNode.containsKey(next);
    }

    public MatchNode createNext(final char next) {
        return nextNode.computeIfAbsent(next, MatchNode::new);
    }

    public MatchNode findNext(final Character next) {
        return nextNode.get(next);
    }

    public void addCharSequence(CharSequence string) {
        MatchNode p = this;
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            AssertUtil.state(c != END_EOF, "不能包含结束字符");
            p = p.createNext(c);
        }

        p.createNext(END_EOF);
    }

    public void addCharSequence(CharSequence string, Object value) {
        MatchNode p = this;
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            AssertUtil.state(c != END_EOF, "不能包含结束字符");
            p = p.createNext(c);
        }

        p.createNext(END_EOF);
        if (p.value != Character.valueOf(END_EOF)) {
            throw new IllegalArgumentException("已设置值: " + string);
        }
        p.value = value;
    }

    public boolean strictMatch(final CharSequence text) {
        MatchNode p = this;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            p = p.findNext(c);
            if (Objects.isNull(p)) {
                return false;
            }
        }

        return p.contains(END_EOF);
    }

    public boolean strictMatchPrefix(final CharSequence text, final char next) {
        MatchNode p = this;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            p = p.findNext(c);
            if (Objects.isNull(p)) {
                return false;
            }
        }

        return p.contains(next);
    }

    public boolean strictMatchPrefix(final CharSequence text) {
        MatchNode p = this;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            p = p.findNext(c);
            if (Objects.isNull(p)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 最大匹配值
     * @param text
     * @return
     */
    public Object maxStrictMatchValue(final CharSequence text) {
        Object ret = null;
        MatchNode p = this;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (p.contains(c)) {
                p = p.findNext(c);
                if (Objects.nonNull(p) && p.value != Character.valueOf(END_EOF)) {
                    ret = p.value;
                }
            } else {
                break;
            }
        }
        return ret;
    }

    /**
     * 最大匹配值
     * @param text
     * @return
     */
    public Object maxStrictMatchValue(final CharSequence text, final Character next) {
        Object ret = null;
        MatchNode p = this;
        int i;
        for (i = 1; i < text.length(); i++) {
            char lastC = text.charAt(i - 1);
            char c = text.charAt(i);
            p = p.findNext(lastC);
            if (Objects.nonNull(p)) {
                // 找到该前缀
                if ((c == next || p.contains(END_EOF) && lastC == next) && p.value != Character.valueOf(END_EOF)) {
                    ret = p.value;
                }
            } else {
                break;
            }
        }

        if (i == text.length()) {
            // 看是否最后一个字符也成功匹配
            // 如果是, 则完全匹配上
            p = p.findNext(text.charAt(i - 1));
            if (Objects.nonNull(p) && p.value != Character.valueOf(END_EOF)) {
                ret = p.value;
            }
        }
        return ret;
    }

    /**
     * 模糊匹配
     * @param text 进行匹配的字符串
     * @return
     */
    public Object indistinctMatchValue(final CharSequence text) {
        return this.indistinctMatchValue(text, '*', '/');
    }

    /**
     * 模糊匹配
     * @param text       进行匹配的字符串
     * @param indistinct 表示模糊的字符
     * @param separator  分割字符
     * @return
     */
    public Object indistinctMatchValue(final CharSequence text, char indistinct, char separator) {
        return indistinctMatchValue(text, 0, this, indistinct, separator);
    }

    private static Object indistinctMatchValue(final CharSequence text, int startIndex, MatchNode p, char indistinct,
                                               char separator) {
        if (Objects.isNull(p)) {
            return null;
        }
        if (startIndex < 0) {
            return p.value;
        }
//        final Stack<Map.Entry<Integer, MatchNode>> stack = new Stack<>();
        final List<Tuple<MatchNode, Integer>> list = new ArrayList<>();
        for (int i = startIndex; i < text.length() && Objects.nonNull(p); i++) {
            char ch = text.charAt(i);
            MatchNode newP = null;
            if (p.contains(ch)) {
                // 存在待匹配字符, 优先匹配
                // 如果匹配失败, 则回溯到上一个模糊字符结点
                newP = p.findNext(ch);
            }
            if (p.contains(indistinct)) {
                // 当前结点的下一个结点可能是模糊字符
                MatchNode inP = p.findNext(indistinct);
                list.add(Tuple.create(inP, i));
            }
            p = newP;
        }

        if (Objects.isNull(p) || p.value == Character.valueOf(END_EOF)) {
            // 没有匹配成功, 进行模糊匹配查找
            // 匹配得越多, 是该值的可能性越高
            Collections.reverse(list);
            return searchPossibleTextIndex(list, text, indistinct, separator);
        } else {
            return p.value;
        }
    }


    private static Object searchPossibleTextIndex(final List<Tuple<MatchNode, Integer>> list, final CharSequence text,
                                                  char indistinct, char separator) {
        // 广度优先搜索
        for (int j = 0; j < list.size(); j++) {
            Tuple<MatchNode, Integer> tuple = list.get(j);
            MatchNode p = tuple.getLeft();
            int startIndex = tuple.getRight();
            if (Objects.isNull(p)) {
                continue;
            }
            if (p.currValue == indistinct && startIndex >= 0) {
                // 当前是模糊匹配
                MatchNode nextP = p.findNext(indistinct);
                for (int i = startIndex + 1; i < text.length(); i++) {
                    char ch = text.charAt(i);
                    if (Objects.nonNull(p)) {
                        list.add(Tuple.create(p.findNext(ch), i + 1));
                    }
                    if (ch == separator) {
                        // 单个模糊字符, 不能模糊匹配分割字符
                        p = null;
                    }
                    if (Objects.nonNull(nextP)) {
                        // 连续两个模糊字符, 代表可以同时模糊匹配分割字符
                        list.add(Tuple.create(nextP.findNext(ch), i + 1));
                    } else if (Objects.isNull(p)) {
                        break;
                    }
                }
                // 后缀全是模糊字符
                if (Objects.nonNull(p) && p.contains(END_EOF)) {
                    list.add(Tuple.create(p, -1));
                }
                if (Objects.nonNull(nextP) && nextP.contains(END_EOF)) {
                    list.add(Tuple.create(nextP, -1));
                }
            } else {
                Object ret = indistinctMatchValue(text, startIndex, p, indistinct, separator);
                if (Objects.nonNull(ret)) {
                    return ret;
                }
            }
        }

        return null;
    }
}
