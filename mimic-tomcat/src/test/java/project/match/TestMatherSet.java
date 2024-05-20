package project.match;

import com.attackonarchitect.XmlComponentScanner;
import com.attackonarchitect.context.ApplicationContext;
import com.attackonarchitect.handler.RouteMaxMatchStrategy;
import com.attackonarchitect.matcher.MatcherSet;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:675464934@qq.com">Terrdi</a>
 * @date 2024/5/16
 * @since 1.8
 **/
public class TestMatherSet {
    @Test
    public void testStrictMatch() {
        MatcherSet set = new MatcherSet();

        set.addCharSequence("test123");
        set.addCharSequence("first");
        set.addCharSequence("test12");
        set.addCharSequence("test");
        set.addCharSequence("test/ab/c/d");


        Assert.assertTrue(set.strictMatch("first"));
        Assert.assertTrue(set.strictMatch("test12"));

        Assert.assertFalse(set.strictMatch("test1234"));
        Assert.assertFalse(set.strictMatch("second"));
    }

    @Test
    public void testMaxStrictMatchValue() {
        MatcherSet set = new MatcherSet();

        Object value = new Object();
        Object default0 = new Object();

        set.addCharSequence("/first/second", value);
        set.addCharSequence("/first", this);
        set.addCharSequence("/", default0);
        set.addCharSequence("test12");
        set.addCharSequence("test");
        set.addCharSequence("test/ab/c/d");
        set.addCharSequence("/test/ab/c/d");

        Assert.assertEquals(this, set.maxStrictMatchValue("/first"));
        Assert.assertEquals(this, set.maxStrictMatchValue("/first/a/b"));
        Assert.assertEquals(this, set.maxStrictMatchValue("/first/c"));
        Assert.assertEquals(this, set.maxStrictMatchValue("/first/c", '/'));
        Assert.assertEquals(value, set.maxStrictMatchValue("/first/second", '/'));
        Assert.assertEquals(value, set.maxStrictMatchValue("/first/second/x", '/'));
        Assert.assertEquals(default0, set.maxStrictMatchValue("/firstc", '/'));
    }

    @Test
    public void testIndistinctMatchValue() {
        MatcherSet set = new MatcherSet();

        set.addCharSequence("/first", 1);
        set.addCharSequence("/*/second", 2);
        set.addCharSequence("/**/third", 3);
        set.addCharSequence("/first/**", 4);

        Assert.assertEquals(1, set.indistinctMatchValue("/first"));
        Assert.assertEquals(2, set.indistinctMatchValue("/first/second"));
        Assert.assertEquals(3, set.indistinctMatchValue("/text/first/second/third"));
        Assert.assertEquals(4, set.indistinctMatchValue("/first/second/third/fourth"));
        Assert.assertNull(set.indistinctMatchValue("/text/first/second"));
    }

    @Test
    public void testPerformance() {
        ApplicationContext context = (ApplicationContext) ApplicationContext.getInstance(new XmlComponentScanner("/WEB-INF/web.xml"),
                null, null);

        RouteMaxMatchStrategy strategy = new RouteMaxMatchStrategy(context);

        Assert.assertEquals(strategy.route("/hello/a/b"), context.getServletMatcher()
                .indistinctMatchValue("/hello/a/b", '*', '\0'));


        long start = System.currentTimeMillis();
        for (int i = 0; i < 10_000_000; i++) {
            strategy.route("/hello/a/b");
        }
        long end = System.currentTimeMillis();
        System.out.println("RouteMaxMatchStrategy#route 耗时 " + (end - start) + "ms.");

        start = System.currentTimeMillis();
        for (int i = 0; i < 10_000_000; i++) {
            context.getServletMatcher()
                    .indistinctMatchValue("/hello/a/b", '*', '\0');
        }
        end = System.currentTimeMillis();
        System.out.println("MatcherSet#indistinctMatchValue 耗时 " + (end - start) + "ms.");
    }
}
