// 代码生成时间: 2025-09-16 03:47:38
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.serviceproxy.ServiceException;

public class WebContentGrabber extends AbstractVerticle {

    private WebClient webClient;

    @Override
    public void start() throws Exception {
        super.start();

        // Configure WebClient options
        WebClientOptions options = new WebClientOptions().setLogActivity(true);
        webClient = WebClient.create(vertx, options);

        // Create a router object
        Router router = Router.router(vertx);

        // Define a route to handle HTTP GET requests to "/fetch"
        router.get="/fetch").handler(this::handleFetch);

        // Start the HTTP server and listen on port 8080
        vertx.createHttpServer().requestHandler(router)
            .listen(config().getInteger("http.port", 8080), result -> {
                if (result.succeeded()) {
                    System.out.println("HTTP server started on port 8080");
                } else {
                    System.err.println("Failed to start the HTTP server");
                }
            });
    }

    private void handleFetch(RoutingContext context) {
        String url = context.request().getParam("url");
        if (url == null) {
            context.response().setStatusCode(400).end("URL parameter is missing");
            return;
        }

        // Fetch the web content
        webClient.getAbs(url).send(ar -> {
            if (ar.succeeded()) {
                HttpResponse<Buffer> response = ar.result();
                if (response.statusCode() == 200) {
                    // Return the fetched content
                    context.response()
                        .putHeader("content-type", "text/html")
                        .end(response.body().toString());
                } else {
                    context.response().setStatusCode(response.statusCode()).end("Failed to fetch content");
                }
            } else {
                // Handle failure scenario
                context.response().setStatusCode(500).end("Error fetching content");
            }
        });
    }

    // Main method to start the Vert.x application
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new WebContentGrabber(), res -> {
            if (res.succeeded()) {
                System.out.println("WebContentGrabber deployed successfully");
            } else {
                System.err.println("Failed to deploy WebContentGrabber");
            }
        });
    }
}
