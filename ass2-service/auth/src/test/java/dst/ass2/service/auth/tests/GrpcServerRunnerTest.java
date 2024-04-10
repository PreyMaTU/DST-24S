package dst.ass2.service.auth.tests;

import dst.ass2.service.auth.AuthenticationServiceApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.Socket;

@ActiveProfiles({"testdata", "grpc"})
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AuthenticationServiceApplication.class)
public class GrpcServerRunnerTest {

    private static final Logger LOG = LoggerFactory.getLogger(GrpcServerRunnerTest.class);

    @Value("${grpc.port}")
    private int port;

    @Test
    public void canConnectToGrpcSocketAfterApplicationInitialization() throws Exception {
        int n = 4;

        while (true) {
            LOG.info("Tyring to connect to TCP socket on localhost:{}", port);
            try (Socket socket = new Socket("localhost", port)) {
                return;
            } catch (Exception e) {
                if (n == 0) {
                    throw new AssertionError("Expected gRPC server to run on port " + port, e);
                } else {
                    Thread.sleep(250);
                }
            }
            n--;
        }

    }
}
