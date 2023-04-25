package org.example.verticles;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.reactivex.rxjava3.core.Completable;
import io.vertx.core.http.HttpMethod;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.core.eventbus.Message;
import io.vertx.rxjava3.ext.web.Router;
import io.vertx.rxjava3.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpVerticle.class);

  @Override
  public Completable rxStart() {
    final var server = vertx.createHttpServer();
    final var router = Router.router(vertx);

    router.route()
        .method(HttpMethod.POST)
        .consumes(HttpHeaderValues.TEXT_PLAIN.toString())
        .produces(HttpHeaderValues.TEXT_PLAIN.toString())
        .path("/example/v1/encrypt")
        .handler(this::handleEncryption);

    router.route().handler(this::handleEncryption);

    return server.requestHandler(router)
        .rxListen(config().getInteger("APP_PORT"))
        .doOnSuccess(any -> LOGGER.info("Application deployed successfully."))
        .ignoreElement();
  }

  private void handleEncryption(final RoutingContext routingContext) {
    routingContext.request().bodyHandler(buffer -> {
      final String plainString = buffer.toString();
      LOGGER.info("Encrypting for the string {}", plainString);
      vertx.eventBus().<String>rxRequest(EncryptionVerticle.ENCRYPTION_EVENT_BUS, plainString)
          .map(Message::body)
          .subscribe(
              routingContext::end,
              error -> routingContext.end(error.getMessage())
          );
    });

  }

  private void handleNotFound(final RoutingContext routingContext) {
    routingContext.response().setStatusCode(404).end();
  }
}
