package dst.ass3.messaging;

import java.util.Objects;

/**
 * Message sent by a worker after it is finished processing a request.
 */
public class WorkerResponse {

    /**
     * The ID of the original {@link TripRequest}.
     */
    private String requestId;

    /**
     * The time it took to process the request (in milliseconds).
     */
    private Long processingTime;

    /**
     * The proposed driver
     */
    private Long driverId;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Long getProcessingTime() {
        return processingTime;
    }

    public void setProcessingTime(Long processingTime) {
        this.processingTime = processingTime;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WorkerResponse that = (WorkerResponse) o;
        return Objects.equals(getRequestId(), that.getRequestId()) &&
            Objects.equals(getProcessingTime(), that.getProcessingTime()) &&
            Objects.equals(getDriverId(), that.getDriverId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRequestId(), getProcessingTime(), getDriverId());
    }

    @Override
    public String toString() {
        return "WorkerResponse{" +
            "requestId='" + requestId + '\'' +
            ", processingTime=" + processingTime +
            ", driverId=" + driverId +
            '}';
    }
}
