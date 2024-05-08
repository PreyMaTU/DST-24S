package dst.ass3.elastic;

import dst.ass3.messaging.Region;

import java.util.List;

public interface IContainerService {

    /**
     * Returns a list of all running containers.
     *
     * @return a list of ContainerInfo objects
     * @throws ContainerException if an error occurred when trying to fetch the running containers.
     */
    List<ContainerInfo> listContainers() throws ContainerException;

    /**
     * Stops the container with the given container ID.
     *
     * @param containerId ID of the container to stop.
     * @throws ContainerNotFoundException if the container to stop is not running
     * @throws ContainerException         if another error occurred when trying to stop the container
     */
    void stopContainer(String containerId) throws ContainerException;

    /**
     * Starts a worker for the specific {@link dst.ass3.messaging.Region}.
     *
     * @param region {@link Region} of the worker to start
     * @return ContainerInfo of the started container / worker
     * @throws ImageNotFoundException if the worker docker image is not available
     * @throws ContainerException     if another error occurred when trying to start the worker
     */
    ContainerInfo startWorker(Region region) throws ContainerException;

}
