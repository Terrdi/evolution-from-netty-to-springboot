package com.attackonarchitect.utils;

import java.util.Objects;

/**
 * 二元组
 *
 * @author <a href="mailto:675464934@qq.com">Terrdi</a>
 * @date 2024/5/17
 * @since 1.8
 **/
public class Tuple<L, R> {
    private final L left;

    private final R right;


    private Tuple(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public static <L, R> Tuple<L, R> create(L left, R right) {
        return new Tuple<>(left, right);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple<?, ?> tuple = (Tuple<?, ?>) o;
        return Objects.equals(left, tuple.left) && Objects.equals(right, tuple.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    public L getLeft() {
        return left;
    }

    public R getRight() {
        return right;
    }
}
