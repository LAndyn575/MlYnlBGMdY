// 代码生成时间: 2025-08-25 19:17:24
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * HTTP请求处理器，使用Vertx框架实现HTTP服务器。
 */
public class HttpRequestHandler extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) {
        // 创建Http服务器
        HttpServer server = vertx.createHttpServer();

        // 创建路由器
        Router router = Router.router(vertx);

        // 设置路由和处理器
        router.route().handler(BodyHandler.create());
        router.route(HttpMethod.GET, "/").handler(this::handleRequest);
        router.route(HttpMethod.POST, "/data").handler(this::handleData);

        // 启动服务器
        server.requestHandler(router)
            .listen(config().getInteger("http.port", 8080), result -> {
            if (result.succeeded()) {
                startFuture.complete();
            } else {
                startFuture.fail(result.cause());
            }
        });
    }

    /**
     * 处理GET请求
     * @param context 路由上下文
     */
    private void handleRequest(RoutingContext context) {
        HttpServerRequest request = context.request();
        HttpServerResponse response = context.response();

        response.putHeader("content-type", "text/plain");
        response.end("Hello from Vert.x!
");
    }

    /**
     * 处理POST请求
     * @param context 路由上下文
     */
    private void handleData(RoutingContext context) {
        context.request().bodyHandler(body -> {
            try {
                JsonObject data = body.toJsonObject();
                context.response()
                        .putHeader("content-type", "application/json")
                        .end(new JsonObject().put("message", "Data received").toString());
            } catch (Exception e) {
                context.response().setStatusCode(400).end("Bad Request
");
            }
        });
    }
}
