package com.attackonarchitect.utils;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.util.AsciiString;

import java.io.File;
import java.util.*;

/**
 * 文件工具类
 *
 * @author <a href="mailto:675464934@qq.com">Terrdi</a>
 * @date 2024/3/18
 * @since 1.8
 **/
public interface FileUtil {
    String WEB_ROOT_PATH = "./WEB-INF";


    /**
     * 解析文件的后缀名
     * @param fileName 文件名称
     * @return
     */
    static String resolveExtension(String fileName) {
        if (StringUtil.isBlank(fileName)) {
            return fileName;
        }

        StringBuilder ret = new StringBuilder(fileName.length());
        for (int i = fileName.length() - 1; i >= 0; i--) {
            char ch = fileName.charAt(i);
            if (ch == '.') {
                i = 0; // break
            } else {
                ret.insert(0, ch);
            }
        }

        return ret.toString();
    }

    Map<String, AsciiString> TEXT_FILE_TYPE = Collections.unmodifiableMap(new HashMap<String, AsciiString>() {
        {
            this.put("html", HttpHeaderValues.TEXT_HTML);
            this.put("txt", HttpHeaderValues.TEXT_PLAIN);
            this.put("json", HttpHeaderValues.APPLICATION_JSON);
            this.put("pdf", AsciiString.cached("application/pdf"));
        }
    });

    Map<String, AsciiString> BINARY_FILE_TYPE = Collections.unmodifiableMap(new HashMap<String, AsciiString>() {
        {
            this.put("doc", HttpHeaderValues.APPLICATION_OCTET_STREAM);
            this.put("zip", HttpHeaderValues.APPLICATION_OCTET_STREAM);
        }
    });

    /**
     * 判断一个文件是否是文本文件(当前通过后缀名判断)
     *
     * @param file 文件
     * @return
     */
    static boolean isTextFile(File file) {
        return TEXT_FILE_TYPE.containsKey(StringUtil.toLowercase(resolveExtension(file.getName())));
    }

    /**
     * 查找指定文件
     *
     * @param path  文件路径
     * @return
     */
    static File resolveFile(String path) {
        File file = new File(WEB_ROOT_PATH, path);
        if (!file.exists()) {
            try {
                file = new File(StringUtil.uriDecode(FileUtil.class.getClassLoader()
                        .getResource(file.getPath()).getFile()));
            } catch (NullPointerException e) {
                System.err.println("NOT FOUND: " + path);
            }
        }
        return file;
    }

    /**
     * 解析文件对应的content-type
     *
     * @param file  文件
     * @return
     */
    static String resolveFileType(File file) {
        final String extension = StringUtil.toLowercase(resolveExtension(file.getName()));

        return Optional.ofNullable(BINARY_FILE_TYPE.get(extension)).orElseGet(() -> TEXT_FILE_TYPE.get(extension))
                .toString();
    }

    /**
     * 解析出文件的简要名称
     *
     * @param fileName
     * @return
     */
    static String getSimpleFileName(final String fileName) {
        int index = Objects.isNull(fileName) ? -1 : fileName.lastIndexOf('.');
        if (index > 0) {
            int sIndex = fileName.lastIndexOf('/');
            if (sIndex < 0) {
                sIndex = fileName.lastIndexOf('\\');
            }
            if (sIndex < 0) {
                sIndex = fileName.lastIndexOf(File.separatorChar);
            }

            return fileName.substring(sIndex + 1, index);
        } else {
            return fileName;
        }
    }
}
