package org.example;

import io.reactivex.rxjava3.core.Single;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.config.ConfigRetriever;
import io.vertx.rxjava3.core.Vertx;

public class ConfigLoader {
  public static Single<JsonObject> getConfiguration(final Vertx vertx) {

    final var fileStore = new ConfigStoreOptions()
        .setType("file")
        .setFormat("properties")
        .setConfig(new JsonObject().put("path", "application.properties"));

    final var envStore = new ConfigStoreOptions()
        .setType("env");

    final var retriever = ConfigRetriever.create(
        vertx,
        new ConfigRetrieverOptions()
            .addStore(fileStore)
            .addStore(envStore)
    );

    return retriever.rxGetConfig();
  }
}
