// 代码生成时间: 2025-09-14 00:44:57
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ServiceBinder;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.concurrent.TimeUnit;

public class SystemPerformanceMonitor extends AbstractVerticle {

    private static final int REFRESH_INTERVAL = 5; // Refresh interval in seconds
    private OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

    @Override
    public void start(Promise<Void> startPromise) {
        // Bind the service to the event bus
        ServiceBinder binder = new ServiceBinder(vertx);
        binder
            .setAddress("system.performance.monitor")
            .register(SystemPerformanceMonitorService.class, this::handle);

        // Schedule the performance monitoring task
        vertx.setPeriodic(TimeUnit.SECONDS.toMillis(REFRESH_INTERVAL), this::monitorPerformance);

        startPromise.complete();
    }

    private void monitorPerformance(long id) {
        try {
            // Collect system performance metrics
            double cpuLoad = osBean.getSystemCpuLoad();
            double memFree = osBean.getFreePhysicalMemorySize();
            double memTotal = osBean.getTotalPhysicalMemorySize();
            double memUsed = memTotal - memFree;
            double memLoad = memUsed / memTotal;

            // Prepare the performance metrics as a JSON object
            JsonObject metrics = new JsonObject()
                .put("cpuLoad", cpuLoad)
                .put("memoryLoad", memLoad);

            // Send the metrics to the event bus
            vertx.eventBus().publish("system.performance.metrics", metrics);
        } catch (Exception e) {
            // Handle any exceptions that occur during monitoring
            vertx.logger().error("Error monitoring system performance", e);
        }
    }

    private void handle(SystemPerformanceMonitorService.MonitorRequest request, Promise<JsonObject> result) {
        // Fetch the latest performance metrics
        double cpuLoad = osBean.getSystemCpuLoad();
        double memLoad = calculateMemoryLoad();

        // Create a JSON object with the metrics and complete the promise
        JsonObject metrics = new JsonObject()
            .put("cpuLoad", cpuLoad)
            .put("memoryLoad", memLoad);

        result.complete(metrics);
    }

    private double calculateMemoryLoad() {
        double memFree = osBean.getFreePhysicalMemorySize();
        double memTotal = osBean.getTotalPhysicalMemorySize();
        double memUsed = memTotal - memFree;
        return memUsed / memTotal;
    }
}

/**
 * A service interface for the system performance monitor.
 */
public interface SystemPerformanceMonitorService {
    /**
     * Fetch the latest system performance metrics.
     */
    void fetchMetrics(MonitorRequest request, Promise<JsonObject> result);

    /**
     * A data object for the monitor request.
     */
    class MonitorRequest {
        // No additional data needed for this request
    }
}
