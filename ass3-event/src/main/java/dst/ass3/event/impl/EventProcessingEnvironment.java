package dst.ass3.event.impl;

import dst.ass3.event.IEventProcessingEnvironment;
import dst.ass3.event.model.events.*;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.windowing.time.Time;

import javax.annotation.processing.ProcessingEnvironment;

public class EventProcessingEnvironment implements IEventProcessingEnvironment {

    private Time timeout;
    private SinkFunction<LifecycleEvent> lifecycleEventSink;
    private SinkFunction<MatchingDuration> matchingDurationSink;
    private SinkFunction<AverageMatchingDuration> averageMatchingDurationSink;
    private SinkFunction<MatchingTimeoutWarning> matchingTimeoutWarningSink;
    private SinkFunction<TripFailedWarning> tripFailedWarningSink;
    private SinkFunction<Alert> alertSink;

    @Override
    public void initialize(StreamExecutionEnvironment env) {
        final var watermarkStrategy= WatermarkStrategy
                .<LifecycleEvent>forMonotonousTimestamps()
                .withTimestampAssigner((event, timestamp) -> event.getTimestamp());

        final var stream = env
                .addSource( new EventSourceFunction() )
                .filter(info -> info.getRegion() != null)
                .map(LifecycleEvent::new)
                .assignTimestampsAndWatermarks(watermarkStrategy);

        stream.addSink(lifecycleEventSink);
    }

    @Override
    public void setMatchingDurationTimeout(Time timeout) {
        this.timeout = timeout;
    }

    @Override
    public void setLifecycleEventStreamSink(SinkFunction<LifecycleEvent> sink) {
        lifecycleEventSink = sink;
    }

    @Override
    public void setMatchingDurationStreamSink(SinkFunction<MatchingDuration> sink) {
        matchingDurationSink= sink;
    }

    @Override
    public void setAverageMatchingDurationStreamSink(SinkFunction<AverageMatchingDuration> sink) {
        averageMatchingDurationSink= sink;
    }

    @Override
    public void setMatchingTimeoutWarningStreamSink(SinkFunction<MatchingTimeoutWarning> sink) {
        matchingTimeoutWarningSink= sink;
    }

    @Override
    public void setTripFailedWarningStreamSink(SinkFunction<TripFailedWarning> sink) {
        tripFailedWarningSink= sink;
    }

    @Override
    public void setAlertStreamSink(SinkFunction<Alert> sink) {
        alertSink= sink;
    }
}
