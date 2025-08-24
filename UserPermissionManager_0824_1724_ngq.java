// 代码生成时间: 2025-08-24 17:24:01
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ServiceBinder;
import io.vertx.serviceproxy.ServiceException;
import io.vertx.serviceproxy.ServiceProxyBuilder;
import io.vertx.serviceproxy.ServiceVerticle;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// 定义服务接口
public interface UserPermissionService {
    String ADDR = "userPermissionServiceAddress";
    void addUser(String username, String permissions, Promise<JsonObject> result);
    void removeUser(String username, Promise<JsonObject> result);
    void updatePermissions(String username, String newPermissions, Promise<JsonObject> result);
    void getUserPermissions(String username, Promise<JsonObject> result);
}

// 实现服务接口的类
public class UserPermissionServiceImpl implements UserPermissionService {
    private Map<String, String> userPermissions = new HashMap<>();

    @Override
    public void addUser(String username, String permissions, Promise<JsonObject> result) {
        if (!userPermissions.containsKey(username)) {
            userPermissions.put(username, permissions);
            result.complete(new JsonObject().put("success", true).put("message", "User added successfully"));
        } else {
            result.fail(new ServiceException(409, "User already exists"));
        }
    }

    @Override
    public void removeUser(String username, Promise<JsonObject> result) {
        if (userPermissions.remove(username) != null) {
            result.complete(new JsonObject().put("success", true).put("message", "User removed successfully"));
        } else {
            result.fail(new ServiceException(404, "User not found"));
        }
    }

    @Override
    public void updatePermissions(String username, String newPermissions, Promise<JsonObject> result) {
        if (userPermissions.containsKey(username)) {
            userPermissions.put(username, newPermissions);
            result.complete(new JsonObject().put("success", true).put("message", "Permissions updated successfully"));
        } else {
            result.fail(new ServiceException(404, "User not found"));
        }
    }

    @Override
    public void getUserPermissions(String username, Promise<JsonObject> result) {
        String permissions = userPermissions.getOrDefault(username, "");
        result.complete(new JsonObject().put("success", true).put("permissions", permissions));
    }
}

// 服务代理生成器
public class UserPermissionServiceVerticle extends AbstractVerticle {
    private ServiceBinder binder;
    private MessageConsumer<JsonObject> consumer;

    @Override
    public void start(Future<Void> startFuture) {
        binder = new ServiceBinder(vertx);
        consumer = binder
            .setAddress(UserPermissionService.ADDR)
            .register(UserPermissionService.class, new UserPermissionServiceImpl());

        startFuture.complete();
    }

    @Override
    public void stop() throws Exception {
        if (consumer != null) {
            consumer.unregister();
        }
    }
}