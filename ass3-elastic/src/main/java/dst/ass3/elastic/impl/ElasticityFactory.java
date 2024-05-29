package dst.ass3.elastic.impl;

import dst.ass3.elastic.IContainerService;
import dst.ass3.elastic.IElasticityController;
import dst.ass3.elastic.IElasticityFactory;
import dst.ass3.messaging.IWorkloadMonitor;

public class ElasticityFactory implements IElasticityFactory {

    @Override
    public IContainerService createContainerService() {
        // TODO
        return null;
    }

    @Override
    public IElasticityController createElasticityController(IContainerService containerService,
                                                            IWorkloadMonitor workloadMonitor) {
        // TODO
        return null;
    }

}
