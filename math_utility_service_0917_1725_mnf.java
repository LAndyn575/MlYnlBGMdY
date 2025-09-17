// 代码生成时间: 2025-09-17 17:25:32
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ServiceBinder;

/**
 * MathUtilityService provides a set of mathematical operations.
 */
public class MathUtilityService extends AbstractVerticle {

    private ServiceBinder binder;

    @Override
    public void start(Future<Void> startFuture) {
        binder = new ServiceBinder(vertx);
        binder
            .setAddress("math.utility")
            .register(MathUtility.class, new MathUtilityImpl());

        startFuture.complete();
    }

    @Override
    public void stop() throws Exception {
        binder.unregister();
    }
}

class MathUtilityImpl implements MathUtility {

    @Override
    public void add(double a, double b, Handler<AsyncResult<JsonObject>> resultHandler) {
        try {
            double sum = a + b;
            resultHandler.handle(Future.succeededFuture(new JsonObject().put("result", sum)));
        } catch (Exception e) {
            resultHandler.handle(Future.failedFuture(e));
        }
    }

    @Override
    public void subtract(double a, double b, Handler<AsyncResult<JsonObject>> resultHandler) {
        try {
            double diff = a - b;
            resultHandler.handle(Future.succeededFuture(new JsonObject().put("result", diff)));
        } catch (Exception e) {
            resultHandler.handle(Future.failedFuture(e));
        }
    }

    @Override
    public void multiply(double a, double b, Handler<AsyncResult<JsonObject>> resultHandler) {
        try {
            double product = a * b;
            resultHandler.handle(Future.succeededFuture(new JsonObject().put("result", product)));
        } catch (Exception e) {
            resultHandler.handle(Future.failedFuture(e));
        }
    }

    @Override
    public void divide(double a, double b, Handler<AsyncResult<JsonObject>> resultHandler) {
        try {
            if (b == 0) {
                throw new IllegalArgumentException("Cannot divide by zero");
            }
            double quotient = a / b;
            resultHandler.handle(Future.succeededFuture(new JsonObject().put("result", quotient)));
        } catch (Exception e) {
            resultHandler.handle(Future.failedFuture(e));
        }
    }
}

/**
 * MathUtility interface defines the contract for mathematical operations.
 */
public interface MathUtility {

    /**
     * Adds two numbers and returns the result.
     *
     * @param a First number
     * @param b Second number
     * @param resultHandler Handler to receive the result
     */
    void add(double a, double b, Handler<AsyncResult<JsonObject>> resultHandler);

    /**
     * Subtracts two numbers and returns the result.
     *
     * @param a First number
     * @param b Second number
     * @param resultHandler Handler to receive the result
     */
    void subtract(double a, double b, Handler<AsyncResult<JsonObject>> resultHandler);

    /**
     * Multiplies two numbers and returns the result.
     *
     * @param a First number
     * @param b Second number
     * @param resultHandler Handler to receive the result
     */
    void multiply(double a, double b, Handler<AsyncResult<JsonObject>> resultHandler);

    /**
     * Divides two numbers and returns the result.
     *
     * @param a First number (numerator)
     * @param b Second number (denominator)
     * @param resultHandler Handler to receive the result
     */
    void divide(double a, double b, Handler<AsyncResult<JsonObject>> resultHandler);
}
