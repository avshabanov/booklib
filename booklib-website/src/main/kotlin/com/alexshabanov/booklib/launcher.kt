package com.alexshabanov.booklib

import com.truward.brikar.server.launcher.StandardLauncher
import org.eclipse.jetty.server.Handler
import org.eclipse.jetty.server.handler.ResourceHandler
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.util.resource.Resource
import java.util.*


private class BooklibLauncher() : StandardLauncher("classpath:/booklibWebsite/") {
  val resourceHandler = createStaticHandler()

  override fun getHandlers(): MutableList<Handler>? {
    val handlers = ArrayList(super.getHandlers())
    handlers.add(resourceHandler)
    return handlers
  }

  override fun isSpringSecurityEnabled() = true

  override fun getServletContextOptions() = ServletContextHandler.SESSIONS

  private fun createStaticHandler(): ResourceHandler {
    val resourceHandler = ResourceHandler()
    resourceHandler.setBaseResource(Resource.newClassPathResource("/booklibWebsite/web/static"))
    return resourceHandler;
  }
}

/** Entry point. */
fun main(args: Array<String>) {
  val launcher = BooklibLauncher()
  launcher.start()
}

