// 代码生成时间: 2025-08-23 09:07:03
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.JksOptions;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.ext.web.sstore.LocalMapSessionStore;
import io.vertx.serviceproxy.ProxyHelper;
import io.vertx.serviceproxy.ServiceProxyBuilder;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.handler.LoggerFormat;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * A Vert.x program that fetches web content and serves it.
 */
public class WebContentFetcher extends AbstractVerticle {

    // Starting the HTTP server
    @Override
    public void start(Future<Void> startFuture) throws Exception {
        Router router = Router.router(vertx);

        // Enable logging for all requests
        router.route().handler(LoggerHandler.create(LoggerFormat.DEFAULT));

        // Serve static files from the "webroot" directory
        router.route("/").handler(StaticHandler.create());

        // Add a route to handle web content fetching
        router.get("/fetch/:url").handler(this::handleFetch);

        // Start the HTTP server on port 8080
        vertx.createHttpServer()
                .requestHandler(router)
                .listen(8080, res -> {
                    if (res.succeeded()) {
                        startFuture.complete();
                    } else {
                        startFuture.fail(res.cause());
                    }
                });
    }

    /**
     * Handles a request to fetch web content from a given URL.
     *
     * @param context The routing context.
     */
    private void handleFetch(RoutingContext context) {
        String url = context.request().getParam("url");
        if (url == null || url.trim().isEmpty()) {
            context.response().setStatusCode(400).end("URL parameter is required");
            return;
        }

        try {
            String content = fetchWebContent(url);
            context.response().setStatusCode(200).end(content);
        } catch (Exception e) {
            context.response().setStatusCode(500).end("Failed to fetch web content: " + e.getMessage());
        }
    }

    /**
     * Fetches web content from a given URL.
     *
     * @param url The URL to fetch content from.
     * @return The fetched web content.
     * @throws IOException If an I/O error occurs.
     */
    private String fetchWebContent(String url) throws IOException {
        try (URL urlObj = new URL(url);
             HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection()) {

            connection.setRequestMethod("GET");
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP response code: " + responseCode);
            }

            try (InputStream inputStream = connection.getInputStream();
                 Reader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {

                StringBuilder builder = new StringBuilder();
                reader.lines().forEach(builder::append);
                return builder.toString();
            }
        }
    }
}
