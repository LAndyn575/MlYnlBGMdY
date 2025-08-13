// 代码生成时间: 2025-08-13 18:04:21
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.serviceproxy.ServiceException;

import static io.vertx.serviceproxy.ProxyHelper.registerService;

// Define a service interface for the math toolbox
public interface MathToolbox extends io.vertx.codegen.annotations.ProxyGen {
    // Method definitions for math operations
    void add(int a, int b, Handler<AsyncResult<Integer>> resultHandler);
    void subtract(int a, int b, Handler<AsyncResult<Integer>> resultHandler);
    void multiply(int a, int b, Handler<AsyncResult<Integer>> resultHandler);
    void divide(int a, int b, Handler<AsyncResult<Double>> resultHandler);
}

// Implement the service interface
public class MathToolboxImpl implements MathToolbox {
    @Override
    public void add(int a, int b, Handler<AsyncResult<Integer>> resultHandler) {
        resultHandler.handle(Future.succeededFuture(a + b));
    }

    @Override
    public void subtract(int a, int b, Handler<AsyncResult<Integer>> resultHandler) {
        resultHandler.handle(Future.succeededFuture(a - b));
    }

    @Override
    public void multiply(int a, int b, Handler<AsyncResult<Integer>> resultHandler) {
        resultHandler.handle(Future.succeededFuture(a * b));
    }

    @Override
    public void divide(int a, int b, Handler<AsyncResult<Double>> resultHandler) {
        if (b == 0) {
            resultHandler.handle(Future.failedFuture(new ServiceException(400, "Cannot divide by zero")));
        } else {
            resultHandler.handle(Future.succeededFuture((double) a / b));
        }
    }
}

public class MathToolboxVerticle extends AbstractVerticle {
    @Override
    public void start(Future<Void> startFuture) {
        Router router = Router.router(vertx);

        // Register the service proxy
        registerService(MathToolbox.class, vertx, new MathToolboxImpl(), res -> {
            if (res.succeeded()) {
                // Create a REST API for the math toolbox
                router.post("/add").handler(BodyHandler.create().setUp());
                router.post("/add").handler(this::handleAdd);

                router.post("/subtract").handler(BodyHandler.create().setUp());
                router.post("/subtract").handler(this::handleSubtract);

                router.post("/multiply").handler(BodyHandler.create().setUp());
                router.post("/multiply").handler(this::handleMultiply);

                router.post("/divide").handler(BodyHandler.create().setUp());
                router.post("/divide").handler(this::handleDivide);

                // Start the web server
                vertx.createHttpServer()
                    .requestHandler(router)
                    .listen(config().getInteger("http.port", 8080), res -> {
                        if (res.succeeded()) {
                            startFuture.complete();
                        } else {
                            startFuture.fail(res.cause());
                        }
                    });
            } else {
                startFuture.fail(res.cause());
            }
        });
    }

    private void handleAdd(RoutingContext context) {
        // Extract parameters and call service
        JsonObject requestBody = context.getBodyAsJson();
        int a = requestBody.getInteger("a");
        int b = requestBody.getInteger("b");
        MathToolbox mathToolbox = MathToolboxAPI.getMathToolboxAPI(vertx);
        mathToolbox.add(a, b, res -> {
            if (res.succeeded()) {
                context.response().setStatusCode(200).end(Json.encodePrettily(new JsonObject().put("result", res.result())));
            } else {
                context.response().setStatusCode(res.cause() instanceof ServiceException ? ((ServiceException) res.cause()).getStatusCode() : 500).end(Json.encodePrettily(new JsonObject().put("error", res.cause().getMessage())));
            }
        });
    }

    private void handleSubtract(RoutingContext context) {
        // Extract parameters and call service
        JsonObject requestBody = context.getBodyAsJson();
        int a = requestBody.getInteger("a");
        int b = requestBody.getInteger("b");
        MathToolbox mathToolbox = MathToolboxAPI.getMathToolboxAPI(vertx);
        mathToolbox.subtract(a, b, res -> {
            if (res.succeeded()) {
                context.response().setStatusCode(200).end(Json.encodePrettily(new JsonObject().put("result", res.result())));
            } else {
                context.response().setStatusCode(res.cause() instanceof ServiceException ? ((ServiceException) res.cause()).getStatusCode() : 500).end(Json.encodePrettily(new JsonObject().put("error", res.cause().getMessage())));
            }
        });
    }

    private void handleMultiply(RoutingContext context) {
        // Extract parameters and call service
        JsonObject requestBody = context.getBodyAsJson();
        int a = requestBody.getInteger("a");
        int b = requestBody.getInteger("b");
        MathToolbox mathToolbox = MathToolboxAPI.getMathToolboxAPI(vertx);
        mathToolbox.multiply(a, b, res -> {
            if (res.succeeded()) {
                context.response().setStatusCode(200).end(Json.encodePrettily(new JsonObject().put("result", res.result())));
            } else {
                context.response().setStatusCode(res.cause() instanceof ServiceException ? ((ServiceException) res.cause()).getStatusCode() : 500).end(Json.encodePrettily(new JsonObject().put("error", res.cause().getMessage())));
            }
        });
    }

    private void handleDivide(RoutingContext context) {
        // Extract parameters and call service
        JsonObject requestBody = context.getBodyAsJson();
        int a = requestBody.getInteger("a");
        int b = requestBody.getInteger("b");
        MathToolbox mathToolbox = MathToolboxAPI.getMathToolboxAPI(vertx);
        mathToolbox.divide(a, b, res -> {
            if (res.succeeded()) {
                context.response().setStatusCode(200).end(Json.encodePrettily(new JsonObject().put("result", res.result())));
            } else {
                context.response().setStatusCode(res.cause() instanceof ServiceException ? ((ServiceException) res.cause()).getStatusCode() : 500).end(Json.encodePrettily(new JsonObject().put("error", res.cause().getMessage())));
            }
        });
    }
}

// This is a utility class to create and retrieve the MathToolbox proxy
public class MathToolboxAPI {
    public static MathToolbox getMathToolboxAPI(Vertx vertx) {
        return registerService(MathToolbox.class, vertx, new MathToolboxImpl());
    }
}