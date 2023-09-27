package io.github.mjhaugsdal.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class MyDecoder extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
//        var header = byteBuf.readCharSequence(4, StandardCharsets.US_ASCII);
        var buf = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(buf);
        list.add(new MyPacket("", buf));
    }
}
