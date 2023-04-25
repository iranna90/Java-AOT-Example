package org.example.verticles;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.reactivex.rxjava3.core.Completable;
import io.vertx.core.http.HttpMethod;
import io.vertx.rxjava3.core.AbstractVerticle;
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
        .consumes(HttpHeaderValues.APPLICATION_JSON.toString())
        .produces(HttpHeaderValues.APPLICATION_JSON.toString())
        .path("/example/v1/encrypt")
        .handler(this::handleEncryption);

    router.route().handler(this::handleEncryption);

    return server.requestHandler(router)
        .rxListen(config().getInteger("APP_PORT"))
        .doOnSuccess(any -> LOGGER.info("Application deployed successfully."))
        .ignoreElement();
  }

  private void handleEncryption(final RoutingContext routingContext) {

  }

  private void handleNotFound(final RoutingContext routingContext) {
    routingContext.response().setStatusCode(404).end();
  }
}
