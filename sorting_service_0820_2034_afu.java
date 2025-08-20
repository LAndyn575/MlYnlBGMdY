// 代码生成时间: 2025-08-20 20:34:18
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * SortingService is a Vert.x Verticle that provides sorting functionality.
 * It can sort a list of integers and return the sorted list as a JSON array.
 */
public class SortingService extends AbstractVerticle {

    /**
     * Starts the Verticle and deploys the HTTP server that handles sorting requests.
     * @param startFuture The future to resolve once the server is started.
     */
    @Override
    public void start(Future<Void> startFuture) {
        vertx.createHttpServer()
            .requestHandler(request -> {
                try {
                    // Parse the request body as a JSON array of integers
                    JsonArray numbers = request.getBodyAsJsonArray();

                    // Check if the input is valid
                    if (numbers == null || numbers.isEmpty()) {
                        request.response().setStatusCode(400).end("Invalid input");
                        return;
                    }

                    // Convert JsonArray to List of Integers
                    List<Integer> numberList = new ArrayList<>();
                    for (Object obj : numbers) {
                        if (!(obj instanceof Number)) {
                            throw new IllegalArgumentException("All elements must be integers");
                        }
                        numberList.add(((Number) obj).intValue());
                    }

                    // Sort the list using Collections.sort
                    Collections.sort(numberList);

                    // Return the sorted list as a JSON array in the response
                    request.response().end(new JsonArray(numberList).encode());
                } catch (Exception e) {
                    // Handle any exceptions and send a 500 error response with the exception message
                    request.response().setStatusCode(500).end(e.getMessage());
                }
            })
            .listen(8080, result -> {
                if (result.succeeded()) {
                    startFuture.complete();
                } else {
                    startFuture.fail(result.cause());
                }
            });
    }
}
