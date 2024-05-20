package com.attackonarchitect.handler;

import com.attackonarchitect.matcher.MatcherSet;
import com.attackonarchitect.servlet.ServletInformation;

/**
 * 从匹配集合中查找
 *
 * @author <a href="mailto:675464934@qq.com">Terrdi</a>
 * @date 2024/5/17
 * @since 1.8
 **/
public class MaxMatchStrategy implements RouteStrategy {
    private final MatcherSet matcherSet;

    public MaxMatchStrategy(MatcherSet matcherSet) {
        this.matcherSet = matcherSet;
    }

    private static final char INDISTINCT_CHAR = '*';

    /**
     * 本算法不存在该字符, 即单个模糊字符就代表多层匹配
     */
    private static final char SEPARATOR_CHAR = '\0';

    @Override
    public ServletInformation route(String uri) {
        return (ServletInformation) this.matcherSet.indistinctMatchValue(uri, INDISTINCT_CHAR, SEPARATOR_CHAR);
    }
}
