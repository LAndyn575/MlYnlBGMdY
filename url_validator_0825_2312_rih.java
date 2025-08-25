// 代码生成时间: 2025-08-25 23:12:59
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import java.net.IDN;
import java.net.URL;
import java.net.UnknownHostException;
import java.net.MalformedURLException;

// URL Validator service using Vertx
public class UrlValidator extends AbstractVerticle {

    private HttpClient client;

    @Override
    public void start(Future<Void> startFuture) {
        client = vertx.createHttpClient();

        Router router = Router.router(vertx);

        // Serve static pages
        router.route("/").handler(StaticHandler.create().setDefaultContentEncoding("UTF-8"));

        // Handle URL validation
        router.post("/validate").handler(this::validateUrl);

        // Start the web server
        vertx.createHttpServer()
            .requestHandler(router)
            .listen(config().getInteger("http.port", 8080), result -> {
                if (result.succeeded()) {
                    startFuture.complete();
                } else {
                    startFuture.fail(result.cause());
                }
            });
    }

    private void validateUrl(RoutingContext context) {
        String urlToValidate = context.getBodyAsString();
        if (urlToValidate == null || urlToValidate.trim().isEmpty()) {
            context.response().setStatusCode(400).end("Invalid request: URL is required");
            return;
        }

        try {
            // Validate URL format
            URL url = new URL(urlToValidate);
            // Convert to punycode if needed
            String punycode = IDN.toASCII(url.getHost());
            // Check if the host can be resolved
            java.net.InetAddress.getByName(punycode);

            // Make an HTTP request to check if the URL is reachable
            HttpClientRequest request = client.request(HttpMethod.HEAD, url.getPort() > 0 ? url.getPort() : 80, punycode, url.getPath());

            request.send(ar -> {
                if (ar.succeeded()) {
                    HttpResponse<Buffer> response = ar.result();
                    int statusCode = response.statusCode();
                    if (statusCode >= 200 && statusCode < 400) {
                        context.response()
                            .putHeader("content-type", "application/json")
                            .end(new JsonObject().put("isValid", true).toString());
                    } else {
                        context.response().setStatusCode(statusCode).end();
                    }
                } else {
                    context.response().setStatusCode(500).end("Failed to validate URL");
                }
            });

        } catch (MalformedURLException e) {
            context.response().setStatusCode(400).end("Invalid URL format");
        } catch (UnknownHostException e) {
            context.response().setStatusCode(404).end("Host not found");
        } catch (Exception e) {
            context.response().setStatusCode(500).end("Internal server error");
        }
    }

    @Override
    public void stop() throws Exception {
        if (client != null) {
            client.close();
        }
    }
}
