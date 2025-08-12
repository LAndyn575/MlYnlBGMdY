// 代码生成时间: 2025-08-12 10:46:02
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.core.Promise;
import io.vertx.core.Promise.promise;

public class WebContentFetcher extends AbstractVerticle {

    private HttpClient client;

    @Override
    public void start(Promise<Void> startPromise) {
        HttpClientOptions options = new HttpClientOptions().setLogActivity(true);
        client = vertx.createHttpClient(options);

        // Deploy the web server verticle
        JsonObject config = new JsonObject().put("port", 8080);
        vertx.deployVerticle(new WebServerVerticle(), new DeploymentOptions().setConfig(config), res -> {
            if (res.succeeded()) {
                startPromise.complete();
            } else {
                startPromise.fail(res.cause());
            }
        });
    }

    // Fetches content from the given URL
    public void fetchContent(String url) {
        HttpClientRequest request = client.get(url, response -> {
            if (response.statusCode() == 200) {
                response.bodyHandler(buffer -> {
                    System.out.println("Fetched content: " + buffer.toString());
                });
            } else {
                System.err.println("Failed to fetch content, status code: " + response.statusCode());
            }
        });

        request.exceptionHandler(err -> {
            System.err.println("An error occurred during the fetch operation: " + err.getMessage());
        });

        request.end();
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        WebContentFetcher fetcher = new WebContentFetcher();
        fetcher.fetchContent("http://example.com");
    }
}

/*
 * WebServerVerticle.java
 * A simple web server verticle that serves as an entry point for our application.
 */
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

public class WebServerVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) {
        int port = config().getInteger("port", 8080);

        HttpServerOptions options = new HttpServerOptions().setPort(port);
        HttpServer server = vertx.createHttpServer(options);
        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());
        router.route("/").handler(StaticHandler.create());

        server.requestHandler(router).listen(ar -> {
            if (ar.succeeded()) {
                startFuture.complete();
            } else {
                startFuture.fail(ar.cause());
            }
        });
    }
}