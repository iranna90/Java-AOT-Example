package org.example;

import io.vertx.core.DeploymentOptions;
import io.vertx.rxjava3.core.Vertx;
import org.example.verticles.EncryptionVerticle;
import org.example.verticles.HttpVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    final var vertx = Vertx.vertx();
    ConfigLoader.getConfiguration(vertx)
        .map(config -> new DeploymentOptions().setConfig(config))
        .flatMap(option -> vertx.rxDeployVerticle(new EncryptionVerticle(), option).map(any -> option))
        .flatMap(option -> vertx.rxDeployVerticle(new HttpVerticle(), option))
        .subscribe(
            success -> LOGGER.info("Successfully deployed."),
            error -> LOGGER.error("Error occurred while deploying.", error)
        );
  }
}