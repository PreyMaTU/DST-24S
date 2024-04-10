package dst.ass2.service.auth;

import dst.ass2.service.auth.grpc.IGrpcServerRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * This class loads the {@link IGrpcServerRunner} from the application context and runs it after the application starts.
 */
public class SpringGrpcServerRunner implements CommandLineRunner, ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(SpringGrpcServerRunner.class);

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(String... args) throws Exception {
        LOG.info("Getting instance of GrpcServerRunner");
        IGrpcServerRunner bean = applicationContext.getBean(IGrpcServerRunner.class);
        LOG.info("Starting IGrpcServerRunner instance {}", bean);
        bean.run();
    }
}
