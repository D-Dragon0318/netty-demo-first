package cn.itcast.server.handler;

import cn.itcast.message.GroupQuitRequestMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @Author: Spridra
 * @CreateTime: 2024-06-29 11:47
 * @Describe:
 * @Version: 1.0
 */

public class GroupQuitRequestMessageHandler extends SimpleChannelInboundHandler<GroupQuitRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupQuitRequestMessage msg) throws Exception {

    }
}
