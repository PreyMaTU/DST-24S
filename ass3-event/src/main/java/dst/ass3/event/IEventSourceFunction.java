package dst.ass3.event;

import dst.ass3.event.model.domain.ITripEventInfo;
import org.apache.flink.api.common.functions.RichFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

/**
 * A RichFunction & SourceFunction for ITripEventInfo objects.
 */
public interface IEventSourceFunction extends RichFunction, SourceFunction<ITripEventInfo> {

    @Override
    void open(Configuration parameters) throws Exception;

    @Override
    void close() throws Exception;

    @Override
    void run(SourceContext<ITripEventInfo> ctx) throws Exception;

    @Override
    void cancel();
}
