package dst.ass3.elastic.impl;

import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import dst.ass3.elastic.ContainerException;
import dst.ass3.elastic.ContainerInfo;
import dst.ass3.elastic.ContainerNotFoundException;
import dst.ass3.elastic.IContainerService;
import dst.ass3.messaging.Region;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ContainerService implements IContainerService {

    private static final String WORKER_IMAGE= "dst/ass3-worker";
    private static final String WORKER_NETWORK= "dst";

    private static final boolean IS_WINDOWS= System.getProperty("os.name").startsWith("Windows");
    private static final String DOCKER_HOST = IS_WINDOWS ?
            "tcp://127.0.0.1:2375" :
            "unix:///var/run/docker.sock";

    private final DockerClientConfig clientConfig;

    ContainerService() {
        clientConfig= DefaultDockerClientConfig
                .createDefaultConfigBuilder()
                .withDockerHost( DOCKER_HOST )
                .build();
    }

    private ContainerInfo containerToInfo(Container container) {
        final var info = new ContainerInfo();

        info.setContainerId(container.getId());
        info.setImage(container.getImage());
        info.setRunning(true);

        if( container.getImage().equals(WORKER_IMAGE) ) {
            final var arguments= container.getCommand().split("\\s+");
            final var region= Region.valueOf(arguments[2].toUpperCase());
            info.setWorkerRegion( region );
        }

        return info;
    }

    @Override
    public List<ContainerInfo> listContainers() throws ContainerException {
        try( final var docker = DockerClientBuilder.getInstance( clientConfig ).build() ) {
            return docker.listContainersCmd().exec()
                    .stream()
                    .map( this::containerToInfo )
                    .collect(Collectors.toList());
        } catch( IOException e ) {
            throw new ContainerException("Could not connect to docker instance");
        }
    }

    @Override
    public void stopContainer(String containerId) throws ContainerException {
        try( final var docker = DockerClientBuilder.getInstance( clientConfig ).build() ) {
            docker.stopContainerCmd( containerId ).exec();

        } catch( NotFoundException e ) {
            throw new ContainerNotFoundException(e.getMessage());
        } catch( IOException e ) {
            throw new ContainerException("Could not connect to docker instance");
        }
    }

    @Override
    public ContainerInfo startWorker(Region region) throws ContainerException {
        try( final var docker = DockerClientBuilder.getInstance( clientConfig ).build() ) {
            final var container = docker
                    .createContainerCmd( WORKER_IMAGE )
                    .withHostConfig( HostConfig
                            .newHostConfig()
                            .withAutoRemove(true)
                            .withNetworkMode( WORKER_NETWORK ))
                    .withCmd(region.name().toLowerCase())
                    .exec();
            docker.startContainerCmd(container.getId()).exec();

            final var info = new ContainerInfo();
            info.setImage( WORKER_IMAGE );
            info.setWorkerRegion(region);
            info.setContainerId(container.getId());

            final var inspect= docker.inspectContainerCmd(container.getId()).exec();
            final var isRunning= inspect.getState().getRunning();
            info.setRunning(isRunning != null && isRunning);

            return info;

        } catch( NotFoundException e ) {
            throw new ContainerNotFoundException(e.getMessage());
        } catch( IOException e ) {
            throw new ContainerException("Could not connect to docker instance");
        }
    }
}
