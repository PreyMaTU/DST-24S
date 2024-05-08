package dst.ass3.event;

import dst.ass3.event.model.events.*;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.windowing.time.Time;

/**
 * This class should be used to implement the event processing steps as described in the assignment. The test classes
 * will inject SinkFunctions, create a new StreamExecutionEnvironment and then call {@link
 * #initialize(StreamExecutionEnvironment)}.
 */
public interface IEventProcessingEnvironment {

    /**
     * Initializes the event processing graph on the {@link StreamExecutionEnvironment}. This function is called
     * after all sinks have been set.
     */
    void initialize(StreamExecutionEnvironment env);

    /**
     * Sets the timeout limit of a streaming event.
     *
     * @param time the timeout limit
     */
    void setMatchingDurationTimeout(Time time);

    void setLifecycleEventStreamSink(SinkFunction<LifecycleEvent> sink);

    void setMatchingDurationStreamSink(SinkFunction<MatchingDuration> sink);

    void setAverageMatchingDurationStreamSink(SinkFunction<AverageMatchingDuration> sink);

    void setMatchingTimeoutWarningStreamSink(SinkFunction<MatchingTimeoutWarning> sink);

    void setTripFailedWarningStreamSink(SinkFunction<TripFailedWarning> sink);

    void setAlertStreamSink(SinkFunction<Alert> sink);
}
