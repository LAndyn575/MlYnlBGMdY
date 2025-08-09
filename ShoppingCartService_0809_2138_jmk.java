// 代码生成时间: 2025-08-09 21:38:02
package com.example.cart;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ServiceBinder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShoppingCartService extends AbstractVerticle implements ShoppingCart {

    private Map<String, JsonArray> carts;

    public ShoppingCartService() {
        carts = new HashMap<>();
    }

    @Override
    public void addProduct(String cartId, JsonObject product, Future<JsonObject> result) {
        if (!carts.containsKey(cartId)) {
            carts.put(cartId, new JsonArray());
        }

        JsonArray products = carts.get(cartId);
        if (products.contains(product)) {
            result.fail(new IllegalArgumentException("Product already exists in the cart"));
            return;
        }

        products.add(product);
        result.complete(new JsonObject().put("cartId", cartId));
    }

    @Override
    public void removeProduct(String cartId, JsonObject product, Future<JsonObject> result) {
        if (!carts.containsKey(cartId)) {
            result.fail(new IllegalArgumentException("Cart does not exist"));
            return;
        }

        JsonArray products = carts.get(cartId);
        int index = products.indexOf(product);
        if (index >= 0) {
            products.remove(index);
            result.complete(new JsonObject().put("cartId", cartId));
        } else {
            result.fail(new IllegalArgumentException("Product does not exist in the cart"));
        }
    }

    @Override
    public void getCart(String cartId, Future<JsonArray> result) {
        if (!carts.containsKey(cartId)) {
            result.fail(new IllegalArgumentException("Cart does not exist"));
            return;
        }

        result.complete(carts.get(cartId));
    }

    @Override
    public void createCart(Future<String> result) {
        String cartId = UUID.randomUUID().toString();
        carts.put(cartId, new JsonArray());
        result.complete(cartId);
    }

    @Override
    public void clearCart(String cartId, Future<Void> result) {
        if (carts.containsKey(cartId)) {
            carts.remove(cartId);
            result.complete();
        } else {
            result.fail(new IllegalArgumentException("Cart does not exist"));
        }
    }

    @Override
    public void start() throws Exception {
        super.start();
        ServiceBinder binder = new ServiceBinder(vertx);
        binder.setAddress(ShoppingCart.ADDRESS)
            .register(ShoppingCart.class, this);
    }
}
