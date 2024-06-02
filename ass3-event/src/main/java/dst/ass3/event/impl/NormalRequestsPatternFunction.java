package dst.ass3.event.impl;

import dst.ass3.event.model.domain.TripState;
import dst.ass3.event.model.events.LifecycleEvent;
import dst.ass3.event.model.events.MatchingDuration;
import dst.ass3.event.model.events.MatchingTimeoutWarning;
import org.apache.flink.cep.functions.PatternProcessFunction;
import org.apache.flink.cep.functions.TimedOutPartialMatchHandler;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.util.List;
import java.util.Map;

public class NormalRequestsPatternFunction extends PatternProcessFunction<LifecycleEvent, MatchingDuration> implements TimedOutPartialMatchHandler<LifecycleEvent> {
    private final OutputTag<MatchingTimeoutWarning> outputTag;

    NormalRequestsPatternFunction(OutputTag<MatchingTimeoutWarning> outputTag) {
        this.outputTag = outputTag;
    }

    static Pattern<LifecycleEvent, ?> createPattern(Time timeout) {
        // Define pattern that expects states in order of: created -> queued? -> matched
        return Pattern
                .<LifecycleEvent>begin("created")
                .where(new SimpleCondition<>() {
                    @Override
                    public boolean filter(LifecycleEvent lifecycleEvent) throws Exception {
                        return lifecycleEvent.getState() == TripState.CREATED;
                    }
                }).next("queued").optional()
                .where(new SimpleCondition<>() {
                    @Override
                    public boolean filter(LifecycleEvent lifecycleEvent) throws Exception {
                        return lifecycleEvent.getState() == TripState.QUEUED;
                    }
                })
                .next("matched")
                .where(new SimpleCondition<>() {
                    @Override
                    public boolean filter(LifecycleEvent lifecycleEvent) throws Exception {
                        return lifecycleEvent.getState() == TripState.MATCHED;
                    }
                }).within( timeout );
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
