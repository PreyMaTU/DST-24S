package dst.ass3.elastic;

import dst.ass3.messaging.IWorkloadMonitor;

public interface IElasticityFactory {
    IContainerService createContainerService();

    IElasticityController createElasticityController(IContainerService containerService,
                                                     IWorkloadMonitor workloadMonitor);
}
