package com.truward.p13n.server;

import com.truward.brikar.server.launcher.StandardLauncher;

/**
 * @author Alexander Shabanov
 */
public final class P13nServiceLauncher {
  public static void main(String[] args) throws Exception {
    new StandardLauncher()
        .setDefaultDirPrefix("classpath:/p13nService/")
        .setSimpleSecurityEnabled(true)
        .setAuthPropertiesPrefix("p13nService.auth")
        .start(args);
  }
}
