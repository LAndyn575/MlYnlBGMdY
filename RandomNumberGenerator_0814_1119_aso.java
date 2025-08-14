// 代码生成时间: 2025-08-14 11:19:13
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.serviceproxy.ServiceException;
import io.vertx.serviceproxy.ServiceProxyBuilder;
import java.util.Random;

public class RandomNumberGenerator extends AbstractVerticle {

    private Random random = new Random();

    @Override
    public void start(Future<Void> startFuture) {
        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());
        router.get("/random").handler(this::generateRandomNumber);

        vertx.createHttpServer()
            .requestHandler(router::accept)
            .listen(config().getInteger("http.port", 8080), result -> {
                if (result.succeeded()) {
                    startFuture.complete();
                } else {
                    startFuture.fail(result.cause());
                }
            });
    }

    private void generateRandomNumber(RoutingContext context) {
        try {
            JsonObject jsonResponse = new JsonObject();
            int randomNumber = random.nextInt();
            jsonResponse.put("randomNumber", randomNumber);
            context.response()
                .putHeader("content-type", "application/json")
                .end(jsonResponse.encode());
        } catch (Exception e) {
            context.response().setStatusCode(500).end("Internal Server Error");
        }
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        ServiceProxyBuilder builder = new ServiceProxyBuilder(vertx);
        // The RandomNumberGeneratorVerticle class is the service interface
        builder.buildRandomNumberGenerator().generateRandomNumber(new JsonObject(), result -> {
            if (result.succeeded()) {
                JsonObject response = result.result();
                System.out.println("You got the random number: " + response.getInteger("randomNumber"));
            } else {
                Throwable cause = result.cause();
                if (cause instanceof ServiceException) {
                    ServiceException serviceException = (ServiceException) cause;
                    int code = serviceException.getStatusCode();
                    System.out.println("Service call failed with status code: " + code);
                } else {
                    System.out.println("Service call failed with exception: " + cause.getMessage());
                }
            }
        });
    }
}
