package com.attackonarchitect.handler;

import com.attackonarchitect.context.Container;
import com.attackonarchitect.context.ServletContext;
import com.attackonarchitect.http.HttpMTRequest;
import com.attackonarchitect.http.HttpMTResponse;
import com.attackonarchitect.listener.Notifier;
import com.attackonarchitect.listener.request.ServletRequestEvent;
import com.attackonarchitect.utils.StringUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Objects;

/**
 * @description:
 *
 */

public class DefaultMimicTomcatChannelHandler extends ChannelInboundHandlerAdapter {
    private Container container;
    public DefaultMimicTomcatChannelHandler(Container container) {
        this.container = container;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        HttpMTRequest request = (HttpMTRequest) msg;
        ServletContext context = (ServletContext) this.container.findChild(StringUtil.resolveUri(request.uri()));
        request.setServletContext(context);

        HttpMTResponse response = new HttpMTResponse(ctx);
        response.setRequest(request);

        ServletRequestEvent servletRequestEvent = new ServletRequestEvent();
//        todo set属性

        this.notifyRequestListener(context, servletRequestEvent);

        this.container.invoke(request, response);

        response.flush();
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        super.exceptionCaught(ctx, cause);
    }

    private void notifyRequestListener(ServletContext servletContext, ServletRequestEvent sre) {
        if (Objects.isNull(servletContext)) {
            return;
        }
        Notifier notifier = (Notifier) servletContext.getAttribute("notifier");
        notifier.notifyListeners(sre);
    }
}
