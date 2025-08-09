// 代码生成时间: 2025-08-10 05:44:45
 * It handles HTTP requests and returns responses based on the client's accept header.
 */

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class ResponsiveWebService extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) {
        try {
            // Create a router instance
            Router router = Router.router(vertx);

            // Handle JSON body for POST requests
            router.route().handler(BodyHandler.create().setUp().
                setBodyLimit(10240)); // 10KB limit

            // Define a route for GET requests to /api/responsive
            router.route(HttpMethod.GET, "/api/responsive").handler(this::handleGetRequest);

            // Create an HTTP server with the defined routes
            HttpServer server = vertx.createHttpServer();
            server.requestHandler(router);

            // Listen on port 8080
            server.listen(8080, result -> {
                if (result.succeeded()) {
                    startFuture.complete();
                } else {
                    startFuture.fail(result.cause());
                }
            });
        } catch (Exception e) {
            startFuture.fail(e);
        }
    }

    /*
     * Handles GET requests to /api/responsive
     * Returns a JSON response indicating the client's accept header
     */
    private void handleGetRequest(RoutingContext context) {
        String acceptHeader = context.request().getHeader("Accept");
        if (acceptHeader == null || acceptHeader.isEmpty()) {
            // If Accept header is missing or empty, respond with a 400 error
            context.response()
                .setStatusCode(400)
                .setStatusMessage("Bad Request")
                .end("Missing or empty Accept header");
            return;
        }

        // Create a JSON object with the Accept header
        JsonObject response = new JsonObject().
            put("status", "success").
            put("acceptHeader", acceptHeader);

        // Set the content type based on the Accept header
        HttpServerResponse response = context.response();
        if (acceptHeader.contains("application/json")) {
            response.putHeader("Content-Type", "application/json");
        } else {
            response.putHeader("Content-Type", "text/plain");
        }

        // End the response with the JSON object
        response.
            setStatusCode(200).
            setStatusMessage("OK").
            end(response.encode());
    }
}
