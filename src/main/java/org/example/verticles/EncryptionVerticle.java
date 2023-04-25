package org.example.verticles;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import io.reactivex.rxjava3.core.Completable;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.core.eventbus.Message;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class EncryptionVerticle extends AbstractVerticle {

  public static final String ENCRYPTION_EVENT_BUS = "event.bus.encryption";

  private Cipher cipher;
  private byte[] key;
  private byte[] iv;
  private String algorithm;

  @Override
  public Completable rxStart() {
    final var eventBus = vertx.eventBus();

    eventBus
        .<String>consumer(ENCRYPTION_EVENT_BUS)
        .handler(this::encrypt);

    this.algorithm = config().getString("ALGORITHM");
    this.key = DatatypeConverter.parseHexBinary(config().getString("ENCRYPTION_KEY"));
    this.iv = DatatypeConverter.parseHexBinary(config().getString("ENCRYPTION_IV"));

    try {
      cipher = Cipher.getInstance(config().getString("TRN"), BouncyCastleProvider.PROVIDER_NAME);
    } catch (Exception e) {
      return Completable.error(e);
    }

    return Completable.complete();
  }

  private void encrypt(final Message<String> message) {
    final var plainString = message.body();
    try {
      cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, algorithm), new IvParameterSpec(iv));
      final byte[] encrypted = cipher.doFinal(plainString.getBytes());
      message.reply(Base64.getEncoder().encodeToString(encrypted));
    } catch (Exception exception) {
      message.reply("Error");
    }
  }
}
