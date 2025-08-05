// 代码生成时间: 2025-08-05 17:39:34
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.buffer.Buffer;
import io.vertx.serviceproxy.ServiceProxyBuilder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

public class HashCalculationTool extends AbstractVerticle {
    /**
     * Starts the Verticle and initializes the service.
     */
    @Override
    public void start(Future<Void> startFuture) {
        // Initialize the service proxy builder with the name and the class of the service.
        ServiceProxyBuilder serviceProxyBuilder = new ServiceProxyBuilder(vertx);
        serviceProxyBuilder.setAddress("hashCalculationServiceAddress");

        // Register the service proxy.
        serviceProxyBuilder.build(HashCalculationService.class, ar -> {
            if (ar.succeeded()) {
                startFuture.complete();
            } else {
                startFuture.fail(ar.cause());
            }
        });
    }

    /**
     * Calculates the hash value of the given input string.
     * 
     * @param algorithm The hash algorithm to use.
     * @param input The input string to hash.
     * @return A string representing the hash value.
     */
    public String calculateHash(String algorithm, String input) {
        try {
            // Get the MessageDigest instance for the specified algorithm.
            MessageDigest digest = MessageDigest.getInstance(algorithm);

            // Convert the input string to bytes using UTF-8 encoding.
            byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);

            // Update the digest with the input bytes.
            digest.update(inputBytes);

            // Calculate the hash value and convert it to a hexadecimal string.
            byte[] hashBytes = digest.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // Handle the exception if the specified hash algorithm is not available.
            throw new RuntimeException("Hash algorithm not found: " + algorithm, e);
        }
    }
}
