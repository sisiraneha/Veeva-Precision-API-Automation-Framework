package com.precision.bdd.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class Configuration {

  private static final Properties PROPS = new Properties();

  static {
    try (InputStream in = Configuration.class.getResourceAsStream("/config.properties")) {
      if (in == null) {
        throw new IllegalStateException("Missing classpath resource: config.properties");
      }
      PROPS.load(in);
    } catch (IOException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  private Configuration() {}

  public static String baseUri() {
    return PROPS.getProperty("base.uri", "https://petstore.swagger.io/v2");
  }
}
