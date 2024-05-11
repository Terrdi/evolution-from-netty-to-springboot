package com.attackonarchitect.handler;

import com.attackonarchitect.ComponentScanner;
import com.attackonarchitect.context.ServletContext;
import com.attackonarchitect.http.HttpMTRequest;
import com.attackonarchitect.http.HttpMTResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @description:
 *
 */

public class DefaultMimicTomcatChannelHandler extends ChannelInboundHandlerAdapter {
    private ServletContext servletContext;
    public DefaultMimicTomcatChannelHandler(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        HttpMTRequest request = (HttpMTRequest) msg;
        HttpMTResponse response = new HttpMTResponse(ctx);
        response.setRequest(request);

        this.servletContext.invoke(request, response);

        ctx.flush();
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        super.exceptionCaught(ctx, cause);
    }
}
