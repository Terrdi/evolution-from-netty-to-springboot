package com.attackonarchitect.matcher;

/**
 * 前缀查找集合
 *
 * @author <a href="mailto:675464934@qq.com">Terrdi</a>
 * @date 2024/5/16
 * @since 1.8
 **/
public class MatcherSet {
    private final MatchNode head = new MatchNode(MatchNode.END_EOF);

    public void addCharSequence(final CharSequence sequence) {
        this.head.addCharSequence(sequence);
    }

    public void addCharSequence(final CharSequence sequence, final Object value) {
        this.head.addCharSequence(sequence, value);
    }

    public boolean strictMatch(final CharSequence text) {
        return this.head.strictMatch(text);
    }

    public boolean strictMatchPrefix(final CharSequence text, final char next) {
        return this.head.strictMatchPrefix(text, next);
    }

    public Object maxStrictMatchValue(final CharSequence text) {
        return head.maxStrictMatchValue(text);
    }

    public Object maxStrictMatchValue(final CharSequence text, final Character next) {
        return this.head.maxStrictMatchValue(text, next);
    }

    public Object indistinctMatchValue(final CharSequence text) {
        return this.head.indistinctMatchValue(text);
    }

    public Object indistinctMatchValue(final CharSequence text, char indistinct, char separator) {
        return this.head.indistinctMatchValue(text, indistinct, separator);
    }
}
