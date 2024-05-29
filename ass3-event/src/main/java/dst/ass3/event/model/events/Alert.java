package dst.ass3.event.model.events;

import dst.ass3.event.model.domain.Region;

import java.io.Serializable;
import java.util.List;

/**
 * A system alert that aggregates several warnings.
 */
public class Alert implements Serializable {

    private static final long serialVersionUID = -4561132671849230635L;

    private Region region;
    private List<Warning> warnings;

    public Alert() {
    }

    public Alert(Region region, List<Warning> warnings) {
        this.region = region;
        this.warnings = warnings;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public List<Warning> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<Warning> warnings) {
        this.warnings = warnings;
    }

    @Override
    public String toString() {
        return "Alert{" +
            "region='" + region + '\'' +
            ", warnings=" + warnings +
            '}';
    }
}
