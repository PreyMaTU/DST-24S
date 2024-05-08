package dst.ass3.event;

import dst.ass3.event.model.domain.ITripEventInfo;
import org.apache.flink.shaded.netty4.io.netty.bootstrap.ServerBootstrap;
import org.apache.flink.shaded.netty4.io.netty.channel.ChannelFuture;
import org.apache.flink.shaded.netty4.io.netty.channel.ChannelInitializer;
import org.apache.flink.shaded.netty4.io.netty.channel.EventLoopGroup;
import org.apache.flink.shaded.netty4.io.netty.channel.group.ChannelGroup;
import org.apache.flink.shaded.netty4.io.netty.channel.group.DefaultChannelGroup;
import org.apache.flink.shaded.netty4.io.netty.channel.nio.NioEventLoopGroup;
import org.apache.flink.shaded.netty4.io.netty.channel.socket.SocketChannel;
import org.apache.flink.shaded.netty4.io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.flink.shaded.netty4.io.netty.handler.codec.serialization.ClassResolvers;
import org.apache.flink.shaded.netty4.io.netty.handler.codec.serialization.ObjectDecoder;
import org.apache.flink.shaded.netty4.io.netty.handler.codec.serialization.ObjectEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

/**
 * An EventPublisher accepts incoming TCP socket connections on a given port and is able to broadcast {@link ITripEventInfo}
 * objects to these clients.
 */
public class EventPublisher {

    private static final Logger LOG = LoggerFactory.getLogger(EventPublisher.class);

    private final Object clientChannelMonitor = new Object();

    private final int port;
    private final AtomicBoolean closed;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ChannelGroup clientChannels;

    public EventPublisher(int port) {
        this.port = port;
        this.closed = new AtomicBoolean(false);
    }

    public int getPort() {
        return port;
    }

    /**
     * Broadcast an event to all listening channels. Does nothing if no clients are connected.
     *
     * @param event the event to publish
     * @throws IllegalStateException if the publisher hasn't been started yet or has been closed
     */
    public void publish(ITripEventInfo event) {
        if (clientChannels == null || closed.get()) {
            throw new IllegalStateException();
        }

        clientChannels.writeAndFlush(event).syncUninterruptibly();

        // wait a bit for event to propagate
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
        }
    }

    /**
     * Like {@link #publish(ITripEventInfo)} but waits for a given number of milliseconds and then passes the current system
     * time to a factory function.
     *
     * @param delay    the delay in ms
     * @param provider the provider
     */
    public void publish(long delay, Function<Long, ITripEventInfo> provider) {
        if (delay > 0) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
            }
        }
        publish(provider.apply(System.currentTimeMillis()));
    }

    /**
     * This method blocks if no clients are connected, and is notified as soon as a client connects. If clients are
     * connected, the method returns immediately.
     */
    public void waitForClients() {
        if (clientChannels.isEmpty()) {
            LOG.debug("Waiting for clients to connect...");
            synchronized (clientChannelMonitor) {
                try {
                    clientChannelMonitor.wait();
                } catch (InterruptedException e) {
                    LOG.debug("Interrupted while waiting on client connections", e);
                }
            }
        }
    }

    public int getConnectedClientCount() {
        if (clientChannels == null || closed.get()) {
            throw new IllegalStateException();
        }
        return clientChannels.size();
    }

    /**
     * Closes all active client connections.
     */
    public void dropClients() {
        if (clientChannels == null || closed.get()) {
            throw new IllegalStateException();
        }
        clientChannels.close().syncUninterruptibly().group().clear();
    }

    /**
     * Start the server and accept incoming connections. Will call {@link #close()} if an error occurs during
     * connection.
     */
    public void start() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        clientChannels = new DefaultChannelGroup(workerGroup.next());

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .childHandler(new ClientChannelInitializer());

        // Bind and start to accept incoming connections
        ChannelFuture f = b.bind(port).addListener(future -> {
            if (!future.isSuccess()) {
                LOG.error("Error while binding socket");
                close();
            }
        }).syncUninterruptibly();
        LOG.info("Accepting connections on {}", f.channel());
    }

    /**
     * Closes all channels and resources.
     */
    public void close() {
        if (closed.compareAndSet(false, true)) {
            LOG.info("Shutting down event loops");
            if (bossGroup != null) {
                bossGroup.shutdownGracefully();
            }
            if (workerGroup != null) {
                workerGroup.shutdownGracefully();
            }
        }
    }

    private class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {
        @Override
        public void initChannel(SocketChannel ch) throws Exception {
            LOG.info("Initializing client channel {}", ch);
            clientChannels.add(ch);

            ch.pipeline()
                .addFirst(new ObjectEncoder())
                .addFirst(new ObjectDecoder(ClassResolvers.cacheDisabled(ClassLoader.getSystemClassLoader())));

            synchronized (clientChannelMonitor) {
                clientChannelMonitor.notifyAll();
            }
        }
    }
}
