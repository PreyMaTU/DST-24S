package dst.ass3.elastic;


import dst.ass3.messaging.Region;

import java.util.Objects;

/**
 * Value type that represents a container.
 */
public class ContainerInfo {

    /**
     * The name of the container image.
     */
    private String image;

    /**
     * The container ID.
     */
    private String containerId;

    /**
     * True if the container is running.
     */
    private boolean running;

    /**
     * If the container is a worker (indicated by the image dst/ass3-worker), then this field should contain the worker
     * region. Otherwise it can be null.
     */
    private Region workerRegion;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public Region getWorkerRegion() {
        return workerRegion;
    }

    public void setWorkerRegion(Region workerRegion) {
        this.workerRegion = workerRegion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ContainerInfo that = (ContainerInfo) o;
        return isRunning() == that.isRunning() &&
            Objects.equals(getImage(), that.getImage()) &&
            Objects.equals(getContainerId(), that.getContainerId()) &&
            getWorkerRegion() == that.getWorkerRegion();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getImage(), getContainerId(), isRunning(), getWorkerRegion());
    }

    @Override
    public String toString() {
        return "ContainerInfo{" +
            "image='" + image + '\'' +
            ", containerId='" + containerId + '\'' +
            ", running=" + running +
            ", workerRegion=" + workerRegion +
            '}';
    }
}
