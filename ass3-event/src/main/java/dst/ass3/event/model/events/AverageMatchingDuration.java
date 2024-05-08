package dst.ass3.event.model.events;

import dst.ass3.event.model.domain.Region;

import java.io.Serializable;

/**
 * The average of several {@link MatchingDuration} values.
 */
public class AverageMatchingDuration implements Serializable {

    private static final long serialVersionUID = -3767582104941550250L;

    private Region region;
    private double duration;

    public AverageMatchingDuration() {
    }

    public AverageMatchingDuration(Region region, double duration) {
        this.region = region;
        this.duration = duration;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "AverageMatchingDuration{" +
            "region='" + region + '\'' +
            ", duration=" + duration +
            '}';
    }
}
