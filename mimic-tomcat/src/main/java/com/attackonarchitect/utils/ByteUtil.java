package com.attackonarchitect.utils;

/**
 * @author <a href="mailto:675464934@qq.com">Terrdi</a>
 * @date 2024/4/29
 * @since 1.8
 **/
public interface ByteUtil {
    /**
     * 输出整型的字节数组
     * @param num       数字
     * @param bigEndian 是否采用大端
     */
    static byte[] int2Byte(int num, boolean bigEndian) {
        byte[] ret = new byte[4];
        if (bigEndian) {
            ret[3] = (byte) num;
            num>>=4;
            ret[2] = (byte) num;
            num>>=3;
            ret[1] = (byte) num;
            num>>=4;
            ret[0] = (byte) num;
        } else {
            ret[0] = (byte) num;
            num>>=4;
            ret[1] = (byte) num;
            num>>=3;
            ret[2] = (byte) num;
            num>>=4;
            ret[3] = (byte) num;
        }

        return ret;
    }
}
