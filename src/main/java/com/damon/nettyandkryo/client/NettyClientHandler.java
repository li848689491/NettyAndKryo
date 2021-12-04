package com.damon.nettyandkryo.client;

import com.damon.nettyandkryo.entity.ResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 处理客户端IO事件
 * @author damon
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<Object> {
    Logger log = LoggerFactory.getLogger(getClass());

    /**
     *
     * @param o 服务端响应的消息
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        ResponseMessage responseMessage = (ResponseMessage) o;
        log.info("Client receive msg: [{}]", responseMessage.toString());
        // 将服务端的返回结果保存到 AttributeMap 上，AttributeMap 可以看作是一个Channel的共享数据源
        // AttributeMap的 key 是AttributeKey，value 是Attribute
        AttributeKey<ResponseMessage> key = AttributeKey.valueOf("responseMessage");
        channelHandlerContext.channel().attr(key).set(responseMessage);
        channelHandlerContext.channel().close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Client caught exception", cause);
        ctx.close();
    }
}
