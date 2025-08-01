// 代码生成时间: 2025-08-01 21:15:29
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.SecurityHandler;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

// XSS Protection Service
public class XssProtectionService extends AbstractVerticle {

    // Define patterns for common XSS attacks
    private static final Pattern SCRIPT_PATTERN = Pattern.compile("<[^>]*script[^>]*>.*?<[^>]*\/script[^>]*>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final Pattern ON_ATTRIBUTE_PATTERN = Pattern.compile("on\w+="[^"]*"|"[^"]*"[^>]*=[^>]*"on\w+"|\'[^']*\'[^>]*=[^>]*\'on\w+\'|=\s*javascript:[^>]*"), Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.COMMENTS);

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        Router router = Router.router(vertx);

        // CORS handler
        router.route().handler(CorsHandler.create("*"));

        // Body handler to handle JSON data
        router.route().handler(BodyHandler.create());

        // Static files handler
        router.route("/static/*").handler(StaticHandler.create());

        // Security handler to prevent XSS attacks
        router.route("/").handler(this::handleXssProtection);

        // Start the server
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

    // Handle XSS protection
    private void handleXssProtection(RoutingContext context) {
        try {
            // Get the request data
            JsonObject requestData = context.getBodyAsJson();

            // Clean the input data to prevent XSS attacks
            String cleanedData = cleanInputData(requestData.encode());

            // Continue with the request processing
            context.next();
        } catch (Exception e) {
            // Handle errors
            context.fail(400, new JsonObject().put("error", "Invalid input data"));
        }
    }

    // Clean input data to prevent XSS attacks
    private String cleanInputData(String input) {
        // Remove script tags
        input = SCRIPT_PATTERN.matcher(input).replaceAll("");

        // Remove event handlers and JavaScript URLs
        input = ON_ATTRIBUTE_PATTERN.matcher(input).replaceAll("");

        return input;
    }
}
