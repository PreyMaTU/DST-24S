package dst.ass3.event.impl;

import dst.ass3.event.IEventProcessingEnvironment;
import dst.ass3.event.model.domain.TripState;
import dst.ass3.event.model.events.*;
import org.apache.flink.api.common.eventtime.Watermark;
import org.apache.flink.api.common.eventtime.WatermarkGenerator;
import org.apache.flink.api.common.eventtime.WatermarkOutput;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.functions.PatternProcessFunction;
import org.apache.flink.cep.functions.TimedOutPartialMatchHandler;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.time.Duration;
import java.util.List;
import java.util.Map;


public class EventProcessingEnvironment implements IEventProcessingEnvironment {
    private Time timeout= Time.seconds(1);
    private SinkFunction<LifecycleEvent> lifecycleEventSink;
    private SinkFunction<MatchingDuration> matchingDurationSink;
    private SinkFunction<AverageMatchingDuration> averageMatchingDurationSink;
    private SinkFunction<MatchingTimeoutWarning> matchingTimeoutWarningSink;
    private SinkFunction<TripFailedWarning> tripFailedWarningSink;
    private SinkFunction<Alert> alertSink;

    @Override
    public void initialize(StreamExecutionEnvironment env) {
        // Setup watermarking based on the event timestamp
        final var watermarkStrategy= WatermarkStrategy
                .forGenerator(context -> new PunctuatedWatermarkGenerator())
                .withTimestampAssigner((event, timestamp) -> event.getTimestamp());

        // Create stream of life cycle events (state changes) for events with a set region
        final var lifeCycleEventStream = env
                .addSource( new EventSourceFunction() )
                .filter(info -> info.getRegion() != null)
                .map(LifecycleEvent::new)
                .assignTimestampsAndWatermarks(watermarkStrategy);

        // Connect the life cycle event sink
        lifeCycleEventStream.addSink(lifecycleEventSink);

        // Group events by their trip id
        final var keyedStream= lifeCycleEventStream.keyBy(LifecycleEvent::getTripId);

        // Process events from normal requests: Consume pattern matches and detect timeouts
        final var timeoutOutputTag= new OutputTag<MatchingTimeoutWarning>("timedout") {};
        final var normalRequestsPatternStream= CEP.pattern( keyedStream, NormalRequestsPatternFunction.createPattern(timeout) );
        final var matchingDurationStream= normalRequestsPatternStream.process( new NormalRequestsPatternFunction(timeoutOutputTag) );

        matchingDurationStream.addSink( matchingDurationSink );
        matchingDurationStream.getSideOutput(timeoutOutputTag).addSink( matchingTimeoutWarningSink );

        // Process events from bad requests
        final var badRequestsPatternStream= CEP.pattern(keyedStream, BadRequestsPatternFunction.createPattern());
        final var tripFailedWarningStream= badRequestsPatternStream.process( new BadRequestsPatternFunction() );

        tripFailedWarningStream.addSink(tripFailedWarningSink);
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

    private static class PunctuatedWatermarkGenerator implements WatermarkGenerator<LifecycleEvent> {
        @Override
        public void onEvent(LifecycleEvent lifecycleEvent, long eventTimestamp, WatermarkOutput output) {
            output.emitWatermark(new Watermark(lifecycleEvent.getTimestamp()));
        }

        @Override
        public void onPeriodicEmit(WatermarkOutput output) {}
    }
}
