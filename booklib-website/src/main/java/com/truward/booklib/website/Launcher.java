package com.truward.booklib.website;

import com.truward.brikar.server.launcher.StandardLauncher;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author Alexander Shabanov
 */
public final class Launcher extends StandardLauncher {
  private final Logger log = LoggerFactory.getLogger(getClass());
  private final ResourceHandler resourceHandler;

  public Launcher() throws IOException {
    resourceHandler = createStaticHandler();
  }

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
  private ResourceHandler createStaticHandler() throws IOException {
    final ResourceHandler resourceHandler = new ResourceHandler();

    final Resource resource; // points to where the static content lives

    final String overrideStaticPath = System.getProperty("booklib.override.staticPath");
    if (overrideStaticPath != null) {
      log.info("Using override path for static resources: {}", overrideStaticPath);
      resource = Resource.newResource(new File(overrideStaticPath));
    } else {
      resource = Resource.newClassPathResource("/web/static");
    }

    resourceHandler.setBaseResource(resource);
    return resourceHandler;
  }
}
