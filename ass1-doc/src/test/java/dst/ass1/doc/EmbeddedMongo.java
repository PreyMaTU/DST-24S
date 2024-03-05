package dst.ass1.doc;

import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.IFeatureAwareVersion;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.tests.MongodForTestsFactory;
import org.junit.rules.ExternalResource;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;

/**
 * JUnit rule that creates an in-memory instance of MongoDB using the flapdoodle Embedded MongoDB.
 */
public class EmbeddedMongo extends ExternalResource {

    private MongodForTestsFactory mongod;

    @Override
    protected void before() throws Throwable {
        requirePort();
        mongod = new MongodFactory(); // starts process in constructor
    }

    @Override
    protected void after() {
        if (mongod != null) {
            mongod.shutdown();
        }
    }

    private void requirePort() throws IOException, IllegalStateException {
        try (ServerSocket ignore = new ServerSocket(27017)) {
            // ignore
        } catch (BindException e) {
            throw new IllegalStateException("Could not bind port 27017 which is necessary to run the MongoDB tests", e);
        }
    }

    public static class MongodFactory extends MongodForTestsFactory {

        public MongodFactory() throws IOException {
            super(Version.Main.V4_0);
        }

        @Override
        protected MongodConfig newMongodConfig(IFeatureAwareVersion version) throws IOException {
            return MongodConfig.builder()
                .net(new Net(27017, false))
                .version(version)
                .build();
        }
    }
}
