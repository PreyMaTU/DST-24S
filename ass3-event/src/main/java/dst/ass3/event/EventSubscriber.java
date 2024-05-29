package dst.ass3.event;

import dst.ass3.event.model.domain.ITripEventInfo;
import org.apache.flink.shaded.netty4.io.netty.bootstrap.Bootstrap;
import org.apache.flink.shaded.netty4.io.netty.channel.*;
import org.apache.flink.shaded.netty4.io.netty.channel.nio.NioEventLoopGroup;
import org.apache.flink.shaded.netty4.io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.flink.shaded.netty4.io.netty.handler.codec.serialization.ClassResolvers;
import org.apache.flink.shaded.netty4.io.netty.handler.codec.serialization.ObjectDecoder;
import org.apache.flink.shaded.netty4.io.netty.handler.codec.serialization.ObjectEncoder;
import org.apache.flink.shaded.netty4.io.netty.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;
import java.net.SocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * An EventSubscriber receives ITripEventInfo objects through a netty SocketChannel. Create and connect an
 * EventSubscriber using {@link #subscribe(SocketAddress)}. To receive events, call {@link #receive()}.
 */
public class EventSubscriber {

    private static final Logger LOG = LoggerFactory.getLogger(EventSubscriber.class);

    private static final ITripEventInfo POISON_PILL = (ITripEventInfo) Proxy.newProxyInstance(
        ITripEventInfo.class.getClassLoader(), new Class[]{ITripEventInfo.class}, (p, m, a) -> null);

    private final SocketAddress publisherAddress;

    private final BlockingQueue<ITripEventInfo> queue;

    private volatile boolean closed;

    private Channel channel;
    private EventLoopGroup loop;

    private EventSubscriber(SocketAddress publisherAddress) {
        this.publisherAddress = publisherAddress;
        this.queue = new LinkedBlockingQueue<>();
    }

    /**
     * Blocks to receive the next ITripEventInfo published into the channel. Returns {@code null} if the underlying
     * channel has been closed or the thread was interrupted.
     *
     * @return the next ITripEventInfo object
     * @throws IllegalStateException thrown if the previous call returned null and the channel was closed
     */
    public ITripEventInfo receive() throws IllegalStateException {
        synchronized (queue) {
            if (closed && queue.isEmpty()) {
                throw new IllegalStateException();
            }
        }

        ITripEventInfo event;
        try {
            event = queue.take();

            if (event == POISON_PILL) {
                return null;
            } else {
                return event;
            }
        } catch (InterruptedException e) {
            return null;
        }
    }

    private Future<?> start() {
        loop = new NioEventLoopGroup();

        channel = new Bootstrap()
            .group(loop)
            .channel(NioSocketChannel.class)
            .option(ChannelOption.TCP_NODELAY, true)
            .handler(new EventSubscriberHandler())
            .connect(publisherAddress) // ChannelFuture
            .addListener(future -> {
                if (!future.isSuccess()) {
                    LOG.error("Error while connecting");
                    close();
                }
            })
            .syncUninterruptibly()
            .channel();

        LOG.info("Connected to channel {}", channel);

        return loop.submit(() -> {
            try {
                channel.closeFuture().sync();
            } catch (InterruptedException e) {
                // noop
            } finally {
                close();
            }
        });
    }

    /**
     * Closes all resources and threads used by the EventSubscriber.
     */
    public void close() {
        try {
            if (loop != null) {
                synchronized (queue) {
                    if (!loop.isShutdown() && !loop.isTerminated() && !loop.isShuttingDown()) {
                        LOG.info("Shutting down event loop");
                        loop.shutdownGracefully();
                    }
                }
            }
        } finally {
            synchronized (queue) {
                if (!closed) {
                    LOG.debug("Adding poison pill to queue");
                    closed = true;
                    queue.add(POISON_PILL);
                }
            }
        }
    }

    /**
     * Creates a new EventSubscriber that connects to given SocketAddress.
     *
     * @param address the socket address
     * @return a new EventSubscriber
     */
    public static EventSubscriber subscribe(SocketAddress address) {
        EventSubscriber eventSubscriber = new EventSubscriber(address);
        eventSubscriber.start();
        return eventSubscriber;
    }

    private class EventSubscriberHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ctx.read();

            if (!(msg instanceof ITripEventInfo)) {
                LOG.error("Unknown message type received {}", msg);
                return;
            }

            synchronized (queue) {
                if (!closed) {
                    queue.add((ITripEventInfo) msg);
                }
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            LOG.error("EventSubscriberHandler caught an exception", cause);
            ctx.close();
            close();
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            ctx.pipeline()
                .addFirst(new ObjectEncoder())
                .addFirst(new ObjectDecoder(ClassResolvers.cacheDisabled(ClassLoader.getSystemClassLoader())));
        }

    }
}

