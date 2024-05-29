package dst.ass3.event.model.events;

import dst.ass3.event.model.domain.Region;

import java.io.Serializable;

/**
 * Base class for region warnings.
 */
public abstract class Warning implements Serializable {

    private static final long serialVersionUID = 273266717303711974L;

    private Region region;

    public Warning() {
    }

    public Warning(Region region) {
        this.region = region;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    @Override
    public String toString() {
        return "Warning{" +
            "region='" + region + '\'' +
            '}';
    }
}
