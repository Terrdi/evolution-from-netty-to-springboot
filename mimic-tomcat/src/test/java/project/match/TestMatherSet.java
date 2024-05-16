package project.match;

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
}
