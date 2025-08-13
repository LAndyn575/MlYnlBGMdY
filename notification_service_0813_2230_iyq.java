// 代码生成时间: 2025-08-13 22:30:42
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ServiceBinder;
import io.vertx.serviceproxy.ServiceException;
import io.vertx.serviceproxy.ProxyHelper;

import java.util.Map;
import java.util.function.Consumer;

// 消息通知服务接口
public interface NotificationService {
    void sendNotification(String message, Map<String, Object> metadata, Handler<AsyncResult<Void>> resultHandler);
}

// 消息通知服务实现
public class NotificationServiceImpl implements NotificationService {

    private final Consumer<JsonObject> notifier;

    public NotificationServiceImpl(Consumer<JsonObject> notifier) {
        this.notifier = notifier;
    }

    @Override
    public void sendNotification(String message, Map<String, Object> metadata, Handler<AsyncResult<Void>> resultHandler) {
        try {
            // 创建通知对象
            JsonObject notification = new JsonObject().put("message", message).put("metadata\, metadata);

            // 发送通知
            notifier.accept(notification);

            // 通知成功，调用结果处理器
            resultHandler.handle(Future.succeededFuture());
        } catch (Exception e) {
            // 通知失败，调用结果处理器
            resultHandler.handle(Future.failedFuture(e));
        }
    }
}

// 消息通知服务部署类
public class NotificationServiceVerticle extends AbstractVerticle {

    private static final String NOTIFICATION_SERVICE_ADDRESS = "notification.service.address";
    private static final String NOTIFICATION_SERVICE_NAME = "notification.service";

    @Override
    public void start(Future<Void> startFuture) {
        // 创建服务代理工厂
        ProxyHelper.registerService(NotificationService.class, vertx, NotificationServiceImpl::new, NOTIFICATION_SERVICE_ADDRESS);

        // 绑定服务到事件总线地址
        new ServiceBinder(vertx)
            .setAddress(NOTIFICATION_SERVICE_ADDRESS)
            .register(NotificationService.class, ProxyHelper.createProxy(vertx, NOTIFICATION_SERVICE_ADDRESS, NotificationService.class));

        // 服务启动成功
        startFuture.complete();
    }
}
