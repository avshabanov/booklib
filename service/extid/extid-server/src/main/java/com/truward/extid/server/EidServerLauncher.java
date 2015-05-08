package com.truward.extid.server;

import com.truward.brikar.server.launcher.StandardLauncher;

/**
 * @author Alexander Shabanov
 */
public final class EidServerLauncher {
  public static void main(String[] args) throws Exception {
    new StandardLauncher()
        .setDefaultDirPrefix("classpath:/eidService/")
        .setSimpleSecurityEnabled(true)
        .setAuthPropertiesPrefix("eidService.auth")
        .start(args);
  }
}
