package dst.ass3.event.impl;

import dst.ass3.event.IEventProcessingEnvironment;
import dst.ass3.event.model.domain.TripState;
import dst.ass3.event.model.events.*;
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

    private static class MyPatternProcessFunction extends PatternProcessFunction<LifecycleEvent, MatchingDuration> implements TimedOutPartialMatchHandler<LifecycleEvent> {
        private final OutputTag<MatchingTimeoutWarning> outputTag;

        MyPatternProcessFunction(OutputTag<MatchingTimeoutWarning> outputTag) {
            this.outputTag = outputTag;
        }

        @Override
        public void processMatch(Map<String, List<LifecycleEvent>> map, Context context, Collector<MatchingDuration> collector) throws Exception {
            final var createdEvent= map.get("created").get(0);
            final var matchedEvent= map.get("matched").get(0);

            final var event= new MatchingDuration(
                    createdEvent.getTripId(),
                    createdEvent.getRegion(),
                    matchedEvent.getTimestamp() - createdEvent.getTimestamp()
            );

            collector.collect(event);
        }

        @Override
        public void processTimedOutMatch(Map<String, List<LifecycleEvent>> map, Context context) throws Exception {
            final var createdEvent= map.get("created").get(0);

            final var event= new MatchingTimeoutWarning(createdEvent.getTripId(), createdEvent.getRegion());
            context.output(outputTag, event);
        }
    }

    private Time timeout= Time.seconds(1);
    private SinkFunction<LifecycleEvent> lifecycleEventSink;
    private SinkFunction<MatchingDuration> matchingDurationSink;
    private SinkFunction<AverageMatchingDuration> averageMatchingDurationSink;
    private SinkFunction<MatchingTimeoutWarning> matchingTimeoutWarningSink;
    private SinkFunction<TripFailedWarning> tripFailedWarningSink;
    private SinkFunction<Alert> alertSink;

    @Override
    public void initialize(StreamExecutionEnvironment env) {
        final var watermarkStrategy= WatermarkStrategy
                .<LifecycleEvent>forBoundedOutOfOrderness(Duration.ofSeconds(2))
                .withTimestampAssigner((event, timestamp) -> event.getTimestamp());

        final var lifeCycleEventStream = env
                .addSource( new EventSourceFunction() )
                .filter(info -> info.getRegion() != null)
                .map(LifecycleEvent::new)
                .assignTimestampsAndWatermarks(watermarkStrategy);

        lifeCycleEventStream.addSink(lifecycleEventSink);

        Pattern<LifecycleEvent, ?> stateMatchingPattern= Pattern
                .<LifecycleEvent>begin("created")
                .where(new SimpleCondition<LifecycleEvent>() {
                    @Override
                    public boolean filter(LifecycleEvent lifecycleEvent) throws Exception {
                        return lifecycleEvent.getState() == TripState.CREATED;
                    }
                }).followedBy("matched")
                .where(new SimpleCondition<LifecycleEvent>() {
                    @Override
                    public boolean filter(LifecycleEvent lifecycleEvent) throws Exception {
                        return lifecycleEvent.getState() == TripState.MATCHED;
                    }
                }).within( timeout );

        final var keyedStream= lifeCycleEventStream.keyBy(LifecycleEvent::getTripId);
        final var patternStream= CEP.pattern( keyedStream, stateMatchingPattern ); //.inProcessingTime();

        final var timeoutOutputTag= new OutputTag<MatchingTimeoutWarning>("timedout") {};

        final var matchingDurationStream= patternStream.process( new MyPatternProcessFunction(timeoutOutputTag) );

        matchingDurationStream.addSink( matchingDurationSink );
        matchingDurationStream.getSideOutput(timeoutOutputTag).addSink( matchingTimeoutWarningSink );
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
