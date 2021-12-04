package com.damon.nettyandkryo.server;

import com.damon.nettyandkryo.codec.NettyKryoDecoder;
import com.damon.nettyandkryo.codec.NettyKryoEncoder;
import com.damon.nettyandkryo.entity.RequestMessage;
import com.damon.nettyandkryo.entity.ResponseMessage;
import com.damon.nettyandkryo.serializer.KryoSerializer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务端 ChannelInitializer
 * 用来增加多个 Handler 处理类到 ChannelPipeline 上，包括编码、解码、SimpleChatServerHandler 等。
 * @author damon
 */

public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {
    Logger log = LoggerFactory.getLogger(getClass());
    KryoSerializer kryoSerializer = new KryoSerializer();
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        // 解码器（服务端对客户端的请求消息进行解码）
        pipeline.addLast("decoder",new NettyKryoDecoder(kryoSerializer, RequestMessage.class));
        // 编码器（服务端对自己向客户端的响应消息进行编码）
        pipeline.addLast("encoder", new NettyKryoEncoder(kryoSerializer, ResponseMessage.class));
        // Handler
        pipeline.addLast("handler", new NettyServerHandler());
        log.info("Client: " + socketChannel.remoteAddress() + " 已连接");
    }
}
