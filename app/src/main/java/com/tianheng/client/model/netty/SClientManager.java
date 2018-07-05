package com.tianheng.client.model.netty;

import android.content.Context;
import android.util.Log;


import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ConnectTimeoutException;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * Created by huyg on 18/1/10.
 */
public class SClientManager {
    private final String TAG = getClass().getSimpleName();
    public static volatile SClientManager instance = null;

    private Context context;
    public boolean isRunning = false;
    public boolean flag = false;
    public boolean isNetty = true;

    private Socket socket;
    private InputStream is;
    private OutputStream os;

    private EventLoopGroup group;
    private Bootstrap bootstrap;
    private Channel channel;
    private ChannelFuture lastWriteFuture;
    private boolean isConnect = false;

    public static SClientManager getInstance() {
        if (instance == null) {
            synchronized (SClientManager.class) {
                if (instance == null) {
                    instance = new SClientManager();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        this.context = context;
        init(context, true);
        Log.i(TAG, "SClientManager is initializing...");
    }

    public void init(Context context, boolean isNetty) {
        this.context = context;
        this.isNetty = isNetty;
    }

    public void start(final String ip, final String port) {
        Log.i(TAG, "SClientManager is starting...");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    startNetty(ip,port);
                }
            }).start();
    }

    public void stop() {
        Log.i(TAG, "SClientManager is stopping...");
        stopNetty();
    }

    private void startNetty(String ip, String port) {
        Log.i(TAG, "netty is starting...");
        if (group == null)
            group = new NioEventLoopGroup();
        if (bootstrap == null)
            bootstrap = new Bootstrap();
        if (!flag) {
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .handler(new SClientInitializer())
            ;
            flag = true;
        }
        try {
            channel = bootstrap.connect(ip, Integer.parseInt(port)).sync().channel();
            isRunning = true;
            Log.e(TAG, "netty connect success");
        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                Log.i(TAG, "netty start failed...InterruptedException");
            } else if (e instanceof ConnectTimeoutException) {
                Log.e(TAG, "netty start failed, connection time out");
            } else if (e instanceof ConnectException) {
                Log.e(TAG, "netty start failed, No route to host");
            }
            e.printStackTrace();
        }
    }

    private void stopNetty() {
        try {
            // Wait until all messages are flushed before closing the channel.
            if (lastWriteFuture != null) {
                Log.i(TAG, "lastWriteFuture is sync...");
                lastWriteFuture.sync();
            }
            if (channel != null) {
                Log.i(TAG, "channel closeFuture...");
                channel.close();
            }
            if (group != null) {
                Log.i(TAG, "MGroup shutdownGracefully...");
                group.shutdownGracefully();
            }
            isRunning = false;
        } catch (InterruptedException e) {
            Log.i(TAG, "netty stop failed...");
            e.printStackTrace();
        }
    }


    public boolean sendFrame(Object frame) {
        if (frame == null) {
            Log.e(TAG, "frame sent failed, it can not be null");
            return false;
        }
        if (channel == null) {
            Log.e(TAG, "frame sent failed, channel is null, is the manager start?");
            return false;
        }
        //2A 1C 02 01 00 00 00 00 00 00 00 00 89 92 38 04 00 6F 0D 00 0F AA FF 06 00 01 80 00 01 02 18 23
        //42, 28, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, -119, -110, 56, 4, 0, 111, 13, 0, 15, -86, -1, 6, 0, 1, -128, 0, 1, 2, 24, 35
        lastWriteFuture = channel.writeAndFlush(frame);
        lastWriteFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                Log.wtf(TAG, "netty send operationComplete ");
            }
        });
        Log.e(TAG, "netty send -->" + frame);
        return true;
    }
}