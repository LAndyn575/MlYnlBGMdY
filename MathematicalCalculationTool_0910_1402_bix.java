// 代码生成时间: 2025-09-10 14:02:06
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

public class MathematicalCalculationTool extends AbstractVerticle {

    private static final String ADDITION = "addition";
    private static final String SUBTRACTION = "subtraction";
    private static final String MULTIPLICATION = "multiplication";
    private static final String DIVISION = "division";

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        // Register the event bus handlers for each operation.
        vertx.eventBus().consumer(ADDITION, this::handleAddition);
        vertx.eventBus().consumer(SUBTRACTION, this::handleSubtraction);
        vertx.eventBus().consumer(MULTIPLICATION, this::handleMultiplication);
        vertx.eventBus().consumer(DIVISION, this::handleDivision);

        startPromise.complete();
    }

    private void handleAddition(Message<JsonObject> message) {
        JsonObject request = message.body();
        double a = request.getDouble("a");
        double b = request.getDouble("b");
        double result = a + b;
        message.reply(new JsonObject().put("result", result));
    }

    private void handleSubtraction(Message<JsonObject> message) {
        JsonObject request = message.body();
        double a = request.getDouble("a");
        double b = request.getDouble("b");
        double result = a - b;
        message.reply(new JsonObject().put("result", result));
    }

    private void handleMultiplication(Message<JsonObject> message) {
        JsonObject request = message.body();
        double a = request.getDouble("a");
        double b = request.getDouble("b");
        double result = a * b;
        message.reply(new JsonObject().put("result", result));
    }

    private void handleDivision(Message<JsonObject> message) {
        JsonObject request = message.body();
        double a = request.getDouble("a");
        double b = request.getDouble("b");
        if (b == 0) {
            message.reply(new JsonObject().put("error", "Cannot divide by zero."));
        } else {
            double result = a / b;
            message.reply(new JsonObject().put("result", result));
        }
    }
}
