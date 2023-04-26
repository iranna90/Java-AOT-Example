package org.example.features;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.graalvm.nativeimage.ImageSingletons;
import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeClassInitialization;
import org.graalvm.nativeimage.impl.RuntimeClassInitializationSupport;

public class BouncyCastleFeature implements Feature {
  /**
   * @param access The supported operations that the feature can perform at this time
   *               Description:
   *               1. Register bouncy castle provider at build time.
   *               2. Re-initializing the DRGB default and NonceAndIV at run time as we need seed for random generators.
   *               We can not use compile time static seed for random generation.
   *               3. We will provide this feature to native-image builder, so that it can use and register bouncy castle(BC) along with SunJCE crypto providers.
   */
  @Override
  public void afterRegistration(AfterRegistrationAccess access) {
    RuntimeClassInitialization.initializeAtBuildTime("org.bouncycastle");
    final var rci = ImageSingletons.lookup(RuntimeClassInitializationSupport.class);
    rci.rerunInitialization("org.bouncycastle.jcajce.provider.drbg.DRBG$Default", "");
    rci.rerunInitialization("org.bouncycastle.jcajce.provider.drbg.DRBG$NonceAndIV", "");
    Security.addProvider(new BouncyCastleProvider());
  }
}