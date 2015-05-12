package com.truward.booklib.website;

import com.truward.brikar.server.launcher.StandardLauncher;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Application entry point.
 *
 * @author Alexander Shabanov
 */
public final class BooklibWebsiteLauncher extends StandardLauncher {
  private final ResourceHandler resourceHandler;

  public BooklibWebsiteLauncher() throws IOException {
    this.resourceHandler = createStaticHandler();
  }

  public static void main(String[] args) throws Exception {
    new BooklibWebsiteLauncher().setDefaultDirPrefix("classpath:/booklibWebsite/").start(args);
  }

  @Override
  protected List<Handler> getHandlers() {
    final List<Handler> handlers = new ArrayList<>();
    handlers.addAll(super.getHandlers());
    handlers.add(resourceHandler);
    return handlers;
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
      LoggerFactory.getLogger(getClass()).info("Using override path for static resources: {}", overrideStaticPath);
      resource = Resource.newResource(new File(overrideStaticPath));
    } else {
      resource = Resource.newClassPathResource("/booklibWebsite/web/static");
    }
    resourceHandler.setBaseResource(resource);
    return resourceHandler;
  }
}
