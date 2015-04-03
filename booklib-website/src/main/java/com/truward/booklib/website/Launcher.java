package com.truward.booklib.website;

import com.truward.brikar.server.launcher.StandardLauncher;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author Alexander Shabanov
 */
public final class Launcher extends StandardLauncher {
  private ResourceHandler resourceHandler = createStaticHandler();

  @Override
  protected List<Handler> getHandlers() {
    final List<Handler> handlers = new ArrayList<>();
    handlers.addAll(super.getHandlers());
    handlers.add(resourceHandler);
    return handlers;
  }

  public static void main(String[] args) throws Exception {
    final Launcher launcher = new Launcher();
    launcher.start(args);
  }

  //
  // Private
  //

  @Nonnull
  private static ResourceHandler createStaticHandler() {
    final ResourceHandler resourceHandler = new ResourceHandler();
    // TODO: another resource for static changes! (FS for development!)
    resourceHandler.setBaseResource(Resource.newClassPathResource("/web/static"));
    return resourceHandler;
  }
}
