package dst.ass3.event.impl;

import dst.ass3.event.Constants;
import dst.ass3.event.EventSubscriber;
import dst.ass3.event.IEventSourceFunction;
import dst.ass3.event.model.domain.ITripEventInfo;
import org.apache.flink.api.common.functions.IterationRuntimeContext;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.configuration.Configuration;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

public class EventSourceFunction implements IEventSourceFunction {

    private EventSubscriber eventSubscriber;
    private RuntimeContext runtimeContext;
    private final AtomicBoolean running = new AtomicBoolean(false);

    @Override
    public void open(Configuration parameters) throws Exception {
        eventSubscriber = EventSubscriber.subscribe(
                new InetSocketAddress(Constants.EVENT_PUBLISHER_PORT)
        );
    }

    @Override
    public void close() throws Exception {
        if( eventSubscriber != null ) {
            eventSubscriber.close();
            eventSubscriber = null;
        }
    }

    @Override
    public void run(SourceContext<ITripEventInfo> ctx) throws Exception {
        running.set(true);
        while( running.get() ) {
            final var tripEventInfo = eventSubscriber.receive();
            if( tripEventInfo == null ) {
                cancel();
                return;
            }

            ctx.collectWithTimestamp(tripEventInfo, tripEventInfo.getTimestamp());
        }
    }

    @Override
    public void cancel() {
        running.set(false);
    }

    @Override
    public RuntimeContext getRuntimeContext() {
        return runtimeContext;
    }

    @Override
    public IterationRuntimeContext getIterationRuntimeContext() {
        throw new IllegalStateException("Not part of an iteration");
    }

    @Override
    public void setRuntimeContext(RuntimeContext runtimeContext) {
        this.runtimeContext= runtimeContext;
    }
}
