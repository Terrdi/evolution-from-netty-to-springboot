package com.attackonarchitect.matcher;

import com.attackonarchitect.utils.AssertUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
                if ((c == next || p.contains(END_EOF) && lastC == next) && p.value != Character.valueOf(END_EOF)) {
                    ret = p.value;
                }
            } else {
                break;
            }
        }

        if (i == text.length()) {
            // 查找到最后, 可以不做下一个结点判断
            p = p.findNext(text.charAt(i - 1));
            if (Objects.nonNull(p) && p.value != Character.valueOf(END_EOF)) {
                ret = p.value;
            }
        }
        return ret;
    }
}
