package project.servlet.subservlet;

import com.attackonarchitect.http.MTRequest;
import com.attackonarchitect.http.MTResponse;
import com.attackonarchitect.servlet.MimicServlet;
import com.attackonarchitect.servlet.WebServlet;
import com.attackonarchitect.utils.ByteUtil;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * 测试chunked
 *
 * @author <a href="mailto:675464934@qq.com">Terrdi</a>
 * @date 2024/4/29
 * @since 1.8
 **/
@WebServlet("/chunked")
public class ChunkedServlet extends MimicServlet {
    @Override
    protected void doPost(MTRequest req, MTResponse response) {
        response.addHeader(HttpHeaderNames.TRANSFER_ENCODING.toString(), HttpHeaderValues.CHUNKED.toString());
        final byte[] lineSeparator = "\r\n".getBytes(StandardCharsets.UTF_8);
        final byte[] text = "Good bye, chunked.".getBytes(StandardCharsets.UTF_8);
//        response.write(Integer.toHexString(text.length).getBytes());
//        response.write(lineSeparator);
        response.write(text);
        response.write(lineSeparator);
//        response.write(Integer.toHexString(0).getBytes());
//        response.write(lineSeparator);
    }

    @Override
    protected void doGet(MTRequest req, MTResponse response) throws UnsupportedEncodingException {
        response.addHeader(HttpHeaderNames.TRANSFER_ENCODING.toString(), HttpHeaderValues.CHUNKED.toString());
        final byte[] lineSeparator = "\r\n".getBytes(StandardCharsets.UTF_8);
        final byte[] text = "Hello world, chunked.".getBytes(StandardCharsets.UTF_8);
//        response.write(ByteUtil.int2Byte(text.length, true));
//        response.write(lineSeparator);
        response.write(text);
        response.write(lineSeparator);
//        response.write(ByteUtil.int2Byte(0, true));
//        response.write(lineSeparator);
    }
}
