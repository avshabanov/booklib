package com.truward.booklib.book.server;

import com.truward.brikar.server.auth.SimpleAuthenticatorUtil;
import com.truward.brikar.server.auth.SimpleServiceUser;
import com.truward.brikar.server.launcher.StandardLauncher;
import org.eclipse.jetty.servlet.ServletContextHandler;

import javax.annotation.Nonnull;
import java.util.Collections;

/**
 * @author Alexander Shabanov
 */
public final class BookServerLauncher extends StandardLauncher {

  public static void main(String[] args) throws Exception {
    new BookServerLauncher().setDefaultDirPrefix("classpath:/bookService/").start(args);
  }

  //
  // Overridden
  //

  @Override
  protected void initServlets(@Nonnull ServletContextHandler contextHandler) {
    super.initServlets(contextHandler);

    contextHandler.setSecurityHandler(SimpleAuthenticatorUtil.newSecurityHandler(Collections.singletonList(
        new SimpleServiceUser("testonly", "test"))));
  }
}
