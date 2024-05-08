package dst.ass3.messaging.impl;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    QueueManagerTest.class,
    RequestGatewayTest.class,
    WorkloadMonitorTest.class
})
public class Ass3_1_Suite {
}
