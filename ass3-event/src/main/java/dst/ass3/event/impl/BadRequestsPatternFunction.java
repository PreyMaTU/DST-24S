package dst.ass3.event.impl;

import dst.ass3.event.model.domain.TripState;
import dst.ass3.event.model.events.LifecycleEvent;
import dst.ass3.event.model.events.TripFailedWarning;
import org.apache.flink.cep.functions.PatternProcessFunction;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;
import org.apache.flink.util.Collector;

import java.util.List;
import java.util.Map;

public class BadRequestsPatternFunction extends PatternProcessFunction<LifecycleEvent, TripFailedWarning> {

    static Pattern<LifecycleEvent, ?> createPattern() {
        // Define pattern that detects bad trip requests
        return Pattern.begin(
                Pattern.<LifecycleEvent>begin("matched")
                        .where(new SimpleCondition<>() {
                            @Override
                            public boolean filter(LifecycleEvent event) {
                                return event.getState() == TripState.MATCHED;
                            }
                        })
                        .next("queued")
                        .where(new SimpleCondition<>() {
                            @Override
                            public boolean filter(LifecycleEvent event) {
                                return event.getState() == TripState.QUEUED;
                            }
                        })
        ).times(3);
    }

    @Override
    public void processMatch(Map<String, List<LifecycleEvent>> map, Context context, Collector<TripFailedWarning> collector) throws Exception {
        LifecycleEvent matched3 = map.get("matched").get(0);
        final var event= new TripFailedWarning(matched3.getTripId(), matched3.getRegion());

        collector.collect(event);
    }
}
