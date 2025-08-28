// 代码生成时间: 2025-08-29 00:34:43
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.validation.RequestParameters;
import io.vertx.ext.web.validation.ValidationHandler;
import io.vertx.ext.web.validation.builder.ParameterProcessor;
import io.vertx.ext.web.validation.impl.ParameterProcessorImpl;
import io.vertx.ext.web.validation.impl.ValidationHandlerImpl;
import io.vertx.ext.web.validation.test.ValidationException;
import static io.vertx.ext.web.validation.builder.ValidationHandlerBuilder.create;

public class FormDataValidator extends AbstractVerticle {
    // 启动方法，配置路由和处理器
    @Override
    public void start(Future<Void> startFuture) throws Exception {
        Router router = Router.router(vertx);

        // 添加Body处理器，以便于获取表单数据
        router.route().handler(BodyHandler.create());

        // 配置表单验证处理器
        ValidationHandler validationHandler = create(ValidationHandlerImpl.create())
            .addProcessor(ParameterProcessorImpl.create()) // 添加参数处理器
            .build(ValidationHandlerImpl::new);

        // 定义路由规则
        router.post("/validate").handler(this::validateRequest);

        // 启动服务器并监听8080端口
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

    // 表单验证方法
    private void validateRequest(RoutingContext context) {
        try {
            // 获取表单数据
            RequestParameters params = RequestParameters.create(context);
            params
                .requiredString("username")
                .requiredString("email")
                .string("age");

            // 验证参数
            params.checkAll();

            // 如果验证通过，继续处理请求
            context.response().setStatusCode(200).end("Validation succeeded");
        } catch (ValidationException e) {
            // 处理验证异常
            context.response().setStatusCode(400).end("Validation failed: " + e.getMessage());
        }
    }
}
