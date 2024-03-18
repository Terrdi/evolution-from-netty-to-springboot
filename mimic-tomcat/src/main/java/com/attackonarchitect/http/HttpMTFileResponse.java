package com.attackonarchitect.http;

import com.attackonarchitect.utils.FileUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Objects;

/**
 * 读取本地文件并返回
 *
 * @author <a href="mailto:675464934@qq.com">Terrdi</a>
 * @date 2024/3/17
 * @since 1.8
 **/
public class HttpMTFileResponse extends HttpMTResponse {
    public HttpMTFileResponse(ChannelHandlerContext ctx) {
        super(ctx);
    }

    public HttpMTFileResponse(HttpMTResponse response, String fileName) {
        this(response, FileUtil.resolveFile(fileName));
    }

    public HttpMTFileResponse(HttpMTResponse response, File file) {
        this(response.ctx);
        this.file = file;
    }

    private File file = null;

    private String uri = "";

    public HttpMTFileResponse buildUri(final String uri) {
        this.uri = uri;
        return this;
    }

    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public void flush() throws UnsupportedEncodingException {
        if (this.checkMaybeReturnError()) {
            if (this.file.isFile()) {
                this.writeFile();
            } else {
                this.writeDirectory();
            }
        }

        super.flush();
    }

    /**
     * 读取文件并返回
     */
    private void writeFile() {
        if (FileUtil.isTextFile(file)) {
            this.writeTextFile();
        } else {
            this.writeBinaryFile();
        }
    }

    private static final int BUFFER_SIZE = 1024;

    private void writeTextFile() {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] bytes = new byte[BUFFER_SIZE];
            for (int offset = fis.read(bytes, 0, BUFFER_SIZE); offset > 0;
                 offset = fis.read(bytes, 0, BUFFER_SIZE)) {
                this.write(bytes, 0, offset);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.addHeader(HttpHeaderNames.CONTENT_TYPE.toString(),
                FileUtil.resolveFileType(file));
    }

    private void writeBinaryFile() {
//        this.write("这是一个二进制文件");

    }

    /**
     * 读取文件夹下的所有文件列表
     */
    private void writeDirectory() {
        StringBuilder html = new StringBuilder(1024);
        html.append("<html>");
        html.append("<head>");
        html.append("<title>").append(this.file.getName()).append("</title>").append("<meta charset=\"UTF-8\">");
        html.append("</head>");
        html.append("<body>");

        html.append("<h2>Index of ").append(this.uri).append("</h2>");
        html.append("<ul>");
        for (String file0 : this.file.list()) {
            html.append("<li>").append("<a href=\"").append(this.uri).append('/').append(file0).append("\">");
            html.append(file0);
            html.append("</a>").append("</li>");
        }
        html.append("</ul>");

        html.append("</body>");
        html.append("</html>");

        this.write(html.toString());
        this.addHeader(HttpHeaderNames.CONTENT_TYPE.toString(), HttpHeaderValues.TEXT_HTML.toString());
    }

    /**
     * 检查文件并设置响应的状态码,
     *
     * @return 文件是否通过了检查
     */
    private boolean checkMaybeReturnError() {
        if (Objects.isNull(file)) {
            this.setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR);
            System.err.println("文件没有成功设置!");
            return false;
        } else if (!file.exists()) {
            this.delegate = new HttpMTFileResponse(this, "404.html");
            this.delegate.setStatusCode(HttpResponseStatus.NOT_FOUND);
            return false;
        } else if (file.isHidden()) {
            this.setStatusCode(HttpResponseStatus.FORBIDDEN);
            return false;
        } else if (!file.canRead()) {
            this.setStatusCode(HttpResponseStatus.UNAUTHORIZED);
            return false;
        }
        return true;
    }
}
