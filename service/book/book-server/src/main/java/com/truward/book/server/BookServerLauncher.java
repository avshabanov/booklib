package com.truward.book.server;

import com.truward.brikar.server.launcher.StandardLauncher;

/**
 * @author Alexander Shabanov
 */
public final class BookServerLauncher {
  public static void main(String[] args) throws Exception {
    new StandardLauncher()
        .setDefaultDirPrefix("classpath:/bookService/")
        .setSimpleSecurityEnabled(true)
        .setAuthPropertiesPrefix("bookService.auth")
        .start(args);
  }
}
