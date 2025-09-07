// 代码生成时间: 2025-09-07 08:26:23
 * memory usage, and disk usage, and prints them to the console.
 */

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.Util;

public class SystemPerformanceMonitor extends AbstractVerticle {

    private static final long UPDATE_INTERVAL = 1000; // Update interval in milliseconds
    private SystemInfo si;
    private HardwareAbstractionLayer hal;
    private OperatingSystem os;

    public void start() throws Exception {
        si = new SystemInfo();
        hal = si.getHardware();
        os = si.getOperatingSystem();

        // Schedule a periodic task to monitor system performance
        vertx.setPeriodic(UPDATE_INTERVAL, id -> {
            monitorSystem();
        });
    }

    private void monitorSystem() {
        try {
            // Get CPU usage
            CentralProcessor cpu = hal.getProcessor();
            double cpuLoad = cpu.getSystemCpuLoad() * 100;

            // Get memory usage
            GlobalMemory memory = hal.getMemory();
            long totalMemory = memory.getTotal();
            long availableMemory = memory.getAvailable();
            long usedMemory = totalMemory - availableMemory;

            // Get disk usage
            long totalDiskSpace = 0;
            long usedDiskSpace = 0;
            for (OSFileStore fs : os.getFileSystem().getFileStores()) {
                totalDiskSpace += fs.getTotalSpace();
                usedDiskSpace += fs.getUsableSpace();
            }

            // Create a JSON object to represent the system performance metrics
            JsonObject metrics = new JsonObject();
            metrics.put("cpuLoad", cpuLoad);
            metrics.put("totalMemory", totalMemory);
            methods.put("usedMemory\, usedMemory);
            methods.put("totalDiskSpace", totalDiskSpace);
            methods.put("usedDiskSpace", usedDiskSpace);

            // Print the system performance metrics to the console
            System.out.println(metrics.encodePrettily());
        } catch (Exception e) {
            // Handle any exceptions that occur during system performance monitoring
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new SystemPerformanceMonitor(), res -> {
            if (res.succeeded()) {
                System.out.println("System Performance Monitor deployed successfully");
            } else {
                System.out.println("Failed to deploy System Performance Monitor: " + res.cause().getMessage());
            }
        });
    }
}