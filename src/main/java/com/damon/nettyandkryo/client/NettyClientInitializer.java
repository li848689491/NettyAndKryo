package com.damon.nettyandkryo.client;

import com.damon.nettyandkryo.codec.NettyKryoDecoder;
import com.damon.nettyandkryo.codec.NettyKryoEncoder;
import com.damon.nettyandkryo.entity.RequestMessage;
import com.damon.nettyandkryo.entity.ResponseMessage;
import com.damon.nettyandkryo.serializer.KryoSerializer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * ChannelInitializer 用来增加多个 Handler 处理类到 ChannelPipeline 上
 * 包括编码、解码、自定义 Handler 等
 * @author damon
 */
public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {
    KryoSerializer kryoSerializer = new KryoSerializer();

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast("decoder",new NettyKryoDecoder(kryoSerializer, ResponseMessage.class));
        pipeline.addLast("encoder",new NettyKryoEncoder(kryoSerializer, RequestMessage.class));
        pipeline.addLast("handler",new NettyClientHandler());
    }
}
