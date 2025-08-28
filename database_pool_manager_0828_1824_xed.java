// 代码生成时间: 2025-08-28 18:24:14
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Transaction;
import io.vertx.core.Future;
import io.vertx.pgclient.PgPool;

import java.util.function.Consumer;

// DatabasePoolManager.java
public class DatabasePoolManager extends AbstractVerticle {

    private SqlClient sqlClient;

    // 启动方法
    @Override
    public void start(Future<Void> startFuture) {
        // 配置数据库连接池选项
        PoolOptions poolOptions = new PoolOptions()
                .setMaxSize(10)
                .setCachePreparedStatements(true)
                .setIdleTimeout(1800);

        // 创建Vert.x PostgreSQL连接池
        PgPool client = PgPool.pool(vertx, new PgPoolOptions()
                .setPort(5432)
                .setHost("localhost")
                .setDatabase("mydatabase")
                .setUser("user")
                .setPassword("password")
                .setPoolOptions(poolOptions)
        );

        // 将SqlClient实例赋值给成员变量
        this.sqlClient = client;

        // 通知启动成功
        startFuture.complete();
    }

    // 提供数据库连接的方法
    public void withConnection(Consumer<Transaction> transactionConsumer) {
        // 从连接池中获取一个事务
        sqlClient.withTransaction(res -> {
            // 检查是否成功获取事务
            if (res.succeeded()) {
                Transaction transaction = res.result();
                try {
                    // 执行传入的事务操作
                    transactionConsumer.accept(transaction);
                } catch (Exception e) {
                    // 错误处理
                    transaction.close();
                    throw new RuntimeException("Failed to execute transaction consumer", e);
                } finally {
                    // 提交事务
                    transaction.commit(ar -> {
                        if (ar.succeeded()) {
                            // 提交成功，关闭事务
                            transaction.close();
                        } else {
                            // 提交失败，回滚事务
                            transaction.rollback(rollbackRes -> transaction.close());
                       }
                   });
                }
            } else {
                // 获取事务失败，错误处理
                res.cause().printStackTrace();
            }
        });
    }

    // 停止方法
    @Override
    public void stop() throws Exception {
        if (sqlClient != null) {
            sqlClient.close();
        }
    }
}
