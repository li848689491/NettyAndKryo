package com.damon.nettyandkryo.server;

import com.damon.nettyandkryo.entity.RequestMessage;
import com.damon.nettyandkryo.entity.ResponseMessage;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *  服务端 ChannelHandler: 处理服务端 I/O 事件
 *  SimpleChannelInboundHandler<String> 中的泛型表示要处理的进站数据的类型
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<Object> {

    Logger log = LoggerFactory.getLogger(getClass());

    //记录服务端接收客户端请求消息次数
    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(1);

    /**
     *  当 ChannelHandler 在处理数据的过程中发生异常时会调用此方法
     *  在大部分情况下，捕获的异常应该被记录下来并且把关联的 channel 给关闭掉。
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("server catch exception",cause);
        ctx.close();
    }

    /**
     * 每当从服务端读到客户端发送过来的请求信息时，调用此方法
     * @param channelHandlerContext channelHandlerContext
     * @param o 客户端发过来的消息对象
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        RequestMessage requestMessage = (RequestMessage) o;
        log.info("Server receive msg: [{}] , times:[{}]", requestMessage, ATOMIC_INTEGER.getAndIncrement());
        //服务端响应消息
        ResponseMessage messageFromServer = new ResponseMessage("message from server");
        ChannelFuture channelFuture = channelHandlerContext.writeAndFlush(messageFromServer);
        channelFuture.addListener(ChannelFutureListener.CLOSE);
    }
}
