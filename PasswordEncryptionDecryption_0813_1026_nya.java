// 代码生成时间: 2025-08-13 10:26:21
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ServiceBinder;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * 密码加密解密工具类
 * 提供密码加密和解密功能
 */
public class PasswordEncryptionDecryption extends AbstractVerticle {

    private ServiceBinder binder;

    @Override
    public void start(Future<Void> startFuture) {
        binder = new ServiceBinder(vertx);
        binder
            .setAddress("password.service")
            .register(PasswordService.class, new PasswordServiceImpl());
        startFuture.complete();
    }

    @Override
    public void stop() throws Exception {
        binder.unregister();
    }
}

class PasswordServiceImpl implements PasswordService {

    @Override
    public void encrypt(JsonObject params, Handler<AsyncResult<JsonObject>> resultHandler) {
        try {
            String password = params.getString("password");
            String key = params.getString("key");
            byte[] passwordBytes = password.getBytes();
            byte[] keyBytes = key.getBytes();
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] encryptedBytes = cipher.doFinal(passwordBytes);
            String encryptedPassword = Base64.getEncoder().encodeToString(encryptedBytes);
            resultHandler.handle(Future.succeededFuture(new JsonObject().put("encryptedPassword", encryptedPassword)));
        } catch (Exception e) {
            resultHandler.handle(Future.failedFuture(e.getMessage()));
        }
    }

    @Override
    public void decrypt(JsonObject params, Handler<AsyncResult<JsonObject>> resultHandler) {
        try {
            String encryptedPassword = params.getString("encryptedPassword");
            String key = params.getString("key");
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedPassword);
            byte[] keyBytes = key.getBytes();
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            String decryptedPassword = new String(decryptedBytes);
            resultHandler.handle(Future.succeededFuture(new JsonObject().put("decryptedPassword", decryptedPassword)));
        } catch (Exception e) {
            resultHandler.handle(Future.failedFuture(e.getMessage()));
        }
    }
}

interface PasswordService {
    void encrypt(JsonObject params, Handler<AsyncResult<JsonObject>> resultHandler);
    void decrypt(JsonObject params, Handler<AsyncResult<JsonObject>> resultHandler);
}
