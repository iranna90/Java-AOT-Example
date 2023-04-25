package org.example;

import java.security.Security;

import io.vertx.core.DeploymentOptions;
import io.vertx.rxjava3.core.Vertx;
import org.apache.log4j.PropertyConfigurator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.example.verticles.EncryptionVerticle;
import org.example.verticles.HttpVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    PropertyConfigurator.configure(Main.class.getClassLoader().getResourceAsStream("log4j2.properties"));
    System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
    Security.addProvider(new BouncyCastleProvider());
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