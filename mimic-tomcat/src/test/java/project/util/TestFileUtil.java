package project.util;

import com.attackonarchitect.utils.FileUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * 文件工具测试类
 *
 * @author <a href="mailto:675464934@qq.com">Terrdi</a>
 * @date 2024/3/18
 * @since 1.8
 **/
public class TestFileUtil {
    @Test
    public void testResolveExtension() {
        Assert.assertEquals("html", FileUtil.resolveExtension("index.html"));
        Assert.assertNull(FileUtil.resolveExtension(null));
        Assert.assertEquals("cx", FileUtil.resolveExtension("1.a.a.a.c.cx"));
        Assert.assertEquals("  ", FileUtil.resolveExtension("  "));
        Assert.assertEquals("doc", FileUtil.resolveExtension(".doc"));
        Assert.assertEquals(" 0", FileUtil.resolveExtension(". 0"));
    }
}
