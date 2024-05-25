package com.attackonarchitect.utils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Objects;

/**
 * 字符串工具类
 *
 * @author <a href="mailto:675464934@qq.com">Terrdi</a>
 * @date 2023/11/16
 * @since 1.8
 **/
public interface StringUtil {
    /**
     * 字符序列是否具有非空格字符
     *
     * @param value 字符序列
     * @return
     */
    static boolean isNotBlank(final CharSequence value) {
        return !isBlank(value);
    }

    /**
     * 字符序列是否全是空格字符或者为null
     *
     * @param value 字符序列
     * @return
     */
    static boolean isBlank(final CharSequence value) {
        return Objects.isNull(value) ||
                value.chars().allMatch(Character::isWhitespace);
    }

    /**
     * 将字符串全部转换成小写
     *
     * @param value
     * @return
     */
    static String toLowercase(final String value) {
        return Objects.isNull(value) ? null : value.toLowerCase(Locale.ROOT);
    }

    /**
     * url 解码
     *
     * @param uri
     * @return
     */
    static String uriDecode(final String uri) {
        char[] uriArray = uri.toCharArray();
        int len = uriArray.length;
        byte[] bytes = new byte[len];
        int offset = 0;
        for (int i = 1; i <= len; i+=3) {
            if (uriArray[i - 1] == '%') {
                bytes[offset++] = hex2Int(uriArray[i], uriArray[i + 1]);
            } else {
                bytes[offset++] = (byte) uriArray[i - 1];
                i-=2;
            }
        }
        return new String(bytes, 0, offset, StandardCharsets.UTF_8);
    }

    /**
     * 十六进制转字节
     *
     * @param ch1   高位字节
     * @param ch2   低位字节
     * @return
     */
    static byte hex2Int(char ch1, char ch2) {
        return (byte) ((hex2Int(ch1) << 4) + hex2Int(ch2));
    }

    /**
     * 十六进制转字节
     *
     * @param ch   字节
     * @return
     */
    static int hex2Int(char ch) {
        ch = (char) (ch - '0');
        if (ch > 10) {
            ch = (char) (ch + '0' - 'A' + 0x0A);
        }

        if (ch > 0x0F) {
            ch = (char) (ch + 'A' - 'a');
        }

        return ch;
    }

    static String resolveUri(String uri) {
        int index = uri.indexOf('?');
        if (!uri.startsWith("/")) {
            uri = "/" + uri;
            index++;
        }
        return index > 0 ? uri.substring(0, index) : uri;
    }

    /**
     * 解析出主要的文件名称, 去除后缀
     * @param fileName
     * @return
     */
    static String resolveSimpleFileName(final String fileName) {
        int startIndex = fileName.lastIndexOf(File.separatorChar);
        int endIndex = fileName.lastIndexOf('.');
        if (endIndex < 0) {
            endIndex = fileName.length();
        }

        return fileName.substring(startIndex + 1, endIndex);
    }
}
