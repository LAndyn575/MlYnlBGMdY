// 代码生成时间: 2025-08-21 21:28:31
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

/**
 * An HTTP request handler using Vert.x framework.
 */
public class HttpHandlerExample extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) {
        Router router = Router.router(vertx);

        // Handle the root path
        router.route("/").handler(this::handleRoot);

        // Serve static files from the 'webroot' directory
        router.route("/static/*").handler(StaticHandler.create());

        // Handle JSON body payload
        router.route().handler(BodyHandler.create());

        // Start an HTTP server and use the router to handle requests
        HttpServer server = vertx.createHttpServer();
        server.requestHandler(router::accept).listen(8080, result -> {
            if (result.succeeded()) {
                startFuture.complete();
            } else {
                startFuture.fail(result.cause());
            }
        });
    }

    /**
     * Handles requests to the root path.
     * @param context The routing context of the HTTP request.
     */
    private void handleRoot(RoutingContext context) {
        HttpServerRequest request = context.request();
        switch (request.method()) {
            case GET:
                context.response()
                    .putHeader("content-type", "text/plain")
                    .end("Welcome to the Vert.x HTTP server!
");
                break;
            default:
                context.response().setStatusCode(405).end("Method Not Allowed
");
                break;
        }
    }
}
