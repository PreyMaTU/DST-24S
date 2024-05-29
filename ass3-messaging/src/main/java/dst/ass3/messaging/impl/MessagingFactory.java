package dst.ass3.messaging.impl;

import dst.ass3.messaging.IMessagingFactory;
import dst.ass3.messaging.IQueueManager;
import dst.ass3.messaging.IRequestGateway;
import dst.ass3.messaging.IWorkloadMonitor;

public class MessagingFactory implements IMessagingFactory {

    @Override
    public IQueueManager createQueueManager() {
        // TODO
        return null;
    }

    @Override
    public IRequestGateway createRequestGateway() {
        // TODO
        return null;
    }

    @Override
    public IWorkloadMonitor createWorkloadMonitor() {
        // TODO
        return null;
    }

    @Override
    public void close() {
        // implement if needed
    }
}
