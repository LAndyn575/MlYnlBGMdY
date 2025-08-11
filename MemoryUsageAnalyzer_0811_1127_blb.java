// 代码生成时间: 2025-08-11 11:27:47
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ServiceBinder;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

/**
 * MemoryUsageAnalyzer is a Vert.x verticle that provides functionality to analyze memory usage.
 */
public class MemoryUsageAnalyzer extends AbstractVerticle {

    private MemoryMXBean memoryMXBean;

    @Override
    public void init(Vertx vertx, Promise<Void> startPromise) {
        super.init(vertx, startPromise);
        this.memoryMXBean = ManagementFactory.getMemoryMXBean();
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        // Bind the service to the event bus
        ServiceBinder binder = new ServiceBinder(vertx);
        binder
            .setAddress("memory.usage.analyze")
            .register(MemoryUsageService.class, new MemoryUsageServiceImpl());
        startPromise.complete();
    }

    /**
     * MemoryUsageService represents the service interface for memory usage analysis.
     */
    public interface MemoryUsageService {
        /**
         * Get the current memory usage.
         *
         * @return A JsonObject containing memory usage information.
         */
        void getMemoryUsage(Handler<AsyncResult<JsonObject>> resultHandler);
    }

    /**
     * MemoryUsageServiceImpl implements the MemoryUsageService.
     */
    public static class MemoryUsageServiceImpl implements MemoryUsageService {

        @Override
        public void getMemoryUsage(Handler<AsyncResult<JsonObject>> resultHandler) {
            try {
                MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
                MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
                MemoryUsage nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();

                JsonObject memoryUsage = new JsonObject()
                    .put("heapUsed", heapMemoryUsage.getUsed())
                    .put("heapMax", heapMemoryUsage.getMax())
                    .put("nonHeapUsed", nonHeapMemoryUsage.getUsed())
                    .put("nonHeapMax", nonHeapMemoryUsage.getMax());

                resultHandler.handle(Future.succeededFuture(memoryUsage));
            } catch (Exception e) {
                // Handle any errors that occur while fetching memory usage
                resultHandler.handle(Future.failedFuture(e));
            }
        }
    }
}
