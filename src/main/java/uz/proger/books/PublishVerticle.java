package uz.proger.books;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;

public class PublishVerticle extends AbstractVerticle {

  private int i = 0;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    vertx.setPeriodic(1000 , handler -> {
      vertx.eventBus().publish("ping-pong",new JsonObject().put("i = ", i++));
    });
  }
}
