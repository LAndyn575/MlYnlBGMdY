// 代码生成时间: 2025-08-22 18:55:00
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.serviceproxy.ServiceProxyBuilder;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

public class HashCalculationTool extends AbstractVerticle {

    // Define the name of the event bus address.
    private static final String EB_ADDRESS = "hash.calculation.tool";

    @Override
    public void start(Future<Void> startFuture) {
        // Register a service on the event bus.
        ServiceProxyBuilder builder = new ServiceProxyBuilder(vertx);
        builder.build(HashCalculationService.class, EB_ADDRESS);
        startFuture.complete();
    }

    // Main method to run the tool.
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new HashCalculationTool(), res -> {
            if (res.succeeded()) {
                System.out.println("Hash calculation tool is running...");
            } else {
                System.err.println("Failed to deploy: " + res.cause());
            }
        });
    }
}

/**
 * Interface for the hash calculation service.
 */
interface HashCalculationService {
    void calculateHash(String input, Promise<String> result);
}

/**
 * Implementation of the hash calculation service.
 */
class HashCalculationServiceImpl implements HashCalculationService {

    @Override
    public void calculateHash(String input, Promise<String> result) {
        try {
            // Use HMAC-SHA256 for hashing.
            String secretKey = "your-secret-key"; // Replace with your actual secret key.
            Mac sha256HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256HMAC.init(secretKeySpec);

            // Calculate the hash.
            byte[] hash = sha256HMAC.doFinal(input.getBytes(StandardCharsets.UTF_8));
            String encodedHash = Base64.getEncoder().encodeToString(hash);

            // Return the hash result.
            result.complete(encodedHash);
        } catch (Exception e) {
            result.fail(e.getMessage());
        }
    }
}