// 代码生成时间: 2025-08-27 07:20:32
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.auth.authentication.UsernamePasswordCredentials;
import io.vertx.ext.auth.impl.http.HashUtil;

import static io.vertx.ext.auth.impl.Codec.base64Encode;

// 用户身份认证服务
public class UserAuthenticationService extends AbstractVerticle {

    private final AuthenticationProvider authProvider;

    public UserAuthenticationService(AuthenticationProvider authProvider) {
        this.authProvider = authProvider;
    }

    // 启动服务
    @Override
    public void start(Future<Void> startFuture) throws Exception {
        // 注册服务
        vertx.eventBus().consumer("user.auth", message -> {
            JsonObject userCredentials = message.body();
            authProvider.authenticate(new JsonObject()
                    .put("username", userCredentials.getString("username"))
                    .put("password", userCredentials.getString("password")), res -> {
                if (res.succeeded()) {
                    User user = res.result();
                    message.reply(new JsonObject().put("status", "success"));
                } else {
                    message.fail(401, res.cause().getMessage());
                }
            });
        });
    }

    // 校验用户凭证
    public static String hashPassword(String password) {
        // 使用Vert.x提供的HashUtil工具类进行密码加密
        return HashUtil.hashSHA256Hex(password);
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        // 模拟的用户认证提供者
        AuthenticationProvider authProvider = context -> {
            // 这里只是一个示例，实际应用中需要连接数据库等持久层来验证用户
            if ("admin".equals(context.config().getString("username")) &&
                "admin".equals(context.config().getString("password"))) {
                context.succeed(User.create(User.createCredentials("admin", "admin")));
            } else {
                context.fail("Authentication failed");
            }
        };

        vertx.deployVerticle(new UserAuthenticationService(authProvider), res -> {
            if (res.succeeded()) {
                System.out.println("User authentication service started successfully");
            } else {
                System.out.println("Failed to start user authentication service");
            }
        });
    }
}