package project.util;

import com.attackonarchitect.utils.StringUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * 字符串测试类
 *
 * @author <a href="mailto:675464934@qq.com">Terrdi</a>
 * @date 2024/3/18
 * @since 1.8
 **/
public class TestStringUtil {
    @Test
    public void testInt2Byte() {
        Assert.assertEquals((byte) 2, StringUtil.hex2Int('2'));
        Assert.assertEquals((byte) 0x0a, StringUtil.hex2Int('a'));
        Assert.assertEquals((byte) 0x0f, StringUtil.hex2Int('F'));
        Assert.assertEquals((byte) 0x0c, StringUtil.hex2Int('c'));
        Assert.assertEquals((byte) 2, StringUtil.hex2Int('0', '2'));
        Assert.assertEquals((byte) 32, StringUtil.hex2Int('2', '0'));
        Assert.assertEquals((byte) 0x22, StringUtil.hex2Int('2', '2'));
    }

    @Test
    public void testUriDecode() {
        Assert.assertEquals("小米汽车专家", StringUtil.uriDecode("%E5%B0%8F%E7%B1%B3%E6%B1%BD%E8%BD%A6%E4%B8%93%E5%AE%B6"));
        Assert.assertEquals("test", StringUtil.uriDecode("test"));
        Assert.assertEquals("小米汽车专家test", StringUtil.uriDecode("%E5%B0%8F%E7%B1%B3%E6%B1%BD%E8%BD%A6%E4%B8%93%E5%AE%B6test"));
        Assert.assertEquals("小米汽车专家tes", StringUtil.uriDecode("%E5%B0%8F%E7%B1%B3%E6%B1%BD%E8%BD%A6%E4%B8%93%E5%AE%B6tes"));
        Assert.assertEquals("小米汽车专家te", StringUtil.uriDecode("%E5%B0%8F%E7%B1%B3%E6%B1%BD%E8%BD%A6%E4%B8%93%E5%AE%B6te"));
        Assert.assertEquals("小米汽车专家t", StringUtil.uriDecode("%E5%B0%8F%E7%B1%B3%E6%B1%BD%E8%BD%A6%E4%B8%93%E5%AE%B6t"));
    }
}
