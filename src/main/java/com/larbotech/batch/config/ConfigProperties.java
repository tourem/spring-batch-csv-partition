package com.larbotech.batch.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "batch")
public class ConfigProperties {

  private String outDirectory;
  private String intDirectory;

  public String getOutDirectory() {
    return outDirectory;
  }

  public void setOutDirectory(String outDirectory) {
    this.outDirectory = outDirectory;
  }

  public String getIntDirectory() {
    return intDirectory;
  }

  public void setIntDirectory(String intDirectory) {
    this.intDirectory = intDirectory;
  }
}
