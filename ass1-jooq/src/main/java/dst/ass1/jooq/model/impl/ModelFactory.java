package dst.ass1.jooq.model.impl;

import dst.ass1.jooq.model.IModelFactory;
import dst.ass1.jooq.model.IRiderPreference;

public class ModelFactory implements IModelFactory {

    @Override
    public IRiderPreference createRiderPreference() {
        return new RiderPreference();
    }
}
