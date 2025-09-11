// 代码生成时间: 2025-09-11 18:29:28
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.serviceproxy.ServiceProxyBuilder;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.AEADBadTagException;
import java.util.Base64;

public class PasswordEncryptionDecryption extends AbstractVerticle {

    // Constants for encryption and decryption
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String SECRET_KEY = "ThisIsASecretKey123";
    private static final String IV = "RandomInitVector";

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        // Handle POST requests to /encrypt
        router.post="/encrypt", this::handleEncrypt);
        // Handle POST requests to /decrypt
        router.post="/decrypt", this::handleDecrypt);

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(config().getInteger("http.port", 8080), result -> {
                    if (result.succeeded()) {
                        startPromise.complete();
                    } else {
                        startPromise.fail(result.cause());
                    }
                });
    }

    private void handleEncrypt(RoutingContext context) {
        String plainText = context.getBodyAsString();
        try {
            String encryptedText = encrypt(plainText);
            context.response().setStatusCode(200).end(new JsonObject().put("encrypted", encryptedText).encode());
        } catch (Exception e) {
            context.response().setStatusCode(500).end(new JsonObject().put("error", e.getMessage()).encode());
        }
    }

    private void handleDecrypt(RoutingContext context) {
        String encryptedText = context.getBodyAsString();
        try {
            String decryptedText = decrypt(encryptedText);
            context.response().setStatusCode(200).end(new JsonObject().put("decrypted", decryptedText).encode());
        } catch (Exception e) {
            context.response().setStatusCode(500).end(new JsonObject().put("error", e.getMessage()).encode());
        }
    }

    /**
     * Encrypts a plain text using AES/CBC/PKCS5Padding algorithm.
     *
     * @param plainText The plain text to be encrypted
     * @return The encrypted text
     * @throws Exception If encryption fails
     */
    private String encrypt(String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(IV.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * Decrypts an encrypted text using AES/CBC/PKCS5Padding algorithm.
     *
     * @param encryptedText The encrypted text to be decrypted
     * @return The decrypted text
     * @throws Exception If decryption fails
     */
    private String decrypt(String encryptedText) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(IV.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
        return new String(decryptedBytes);
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        new ServiceProxyBuilder(vertx)
                .build(PasswordEncryptionDecryption.class, "password.service");
        vertx.deployVerticle(new PasswordEncryptionDecryption());
    }
}
