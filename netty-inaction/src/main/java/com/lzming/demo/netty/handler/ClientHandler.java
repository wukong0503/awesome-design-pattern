package com.lzming.demo.netty.handler;

import com.lzming.demo.netty.protocol.*;
import com.lzming.demo.netty.util.LoginUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println(new SimpleDateFormat("HH:mm:ss").format(new Date()) + ": 客户端开始登陆");

        // 创建登陆对象
        LoginRequestPacket packet = new LoginRequestPacket();
        packet.setUserId(123456);
        packet.setUsername("guest");
        packet.setPassword("guest");

        ByteBuf buf = PacketCodeC.INSTANCE.encode(ctx.alloc(), packet);
        ctx.channel().writeAndFlush(buf);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf buf = (ByteBuf) msg;

        Packet packet = PacketCodeC.INSTANCE.decode(buf);
        if (packet instanceof LoginResponsePacket) {
            LoginResponsePacket responsePacket = (LoginResponsePacket) packet;
            if (responsePacket.isSuccess()) {
                System.out.println(new SimpleDateFormat("HH:mm:ss").format(new Date()) + ": 客户端登录成功");
                LoginUtil.markAsLogin(ctx.channel());

            } else {
                System.out.println(new SimpleDateFormat("HH:mm:ss").format(new Date())
                        + ": 客户端登录失败，原因：" + responsePacket.getReason());
            }
        } else if (packet instanceof MessageResponsePacket) {
            MessageResponsePacket messageResponsePacket = (MessageResponsePacket) packet;
            System.out.println(new Date() + ": 收到服务端的消息: " + messageResponsePacket.getMessage());
        }
    }
}
