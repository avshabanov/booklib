package com.alexshabanov.booklib.launcher

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.DefaultServlet
import org.springframework.web.filter.CharacterEncodingFilter
import java.util.EnumSet
import javax.servlet.DispatcherType
import org.springframework.web.context.ContextLoaderListener
import org.springframework.web.servlet.DispatcherServlet
import org.eclipse.jetty.server.handler.ResourceHandler
import org.eclipse.jetty.server.handler.HandlerCollection
import org.eclipse.jetty.server.Handler
import org.eclipse.jetty.util.resource.Resource


private fun initSpringContext(context: ServletContextHandler) {
  context.setInitParameter("contextConfigLocation", "classpath:/spring/service-context.xml")

  val cef = context.addFilter(javaClass<CharacterEncodingFilter>(), "/*", EnumSet.allOf(javaClass<DispatcherType>()))
  cef.setInitParameter("encoding", "UTF-8")
  cef.setInitParameter("forceEncoding", "true")

  context.addEventListener(ContextLoaderListener())

  val ds = context.addServlet(javaClass<DispatcherServlet>(), "/g/*,/rest/*")
  ds.setInitParameter("contextConfigLocation", "classpath:/spring/webmvc-context.xml")
}

/**
 * @author Alexander Shabanov
 */
fun main(args: Array<String>) {
  var configPath = "classpath:settings/default.properties"
  System.setProperty("booklib.settings.path", configPath)

  val server = Server(8080)

  val resourceHandler = ResourceHandler()
  resourceHandler.setBaseResource(Resource.newClassPathResource("/web/static"))

  val contextHandler = ServletContextHandler(ServletContextHandler.SESSIONS or ServletContextHandler.NO_SECURITY)
  contextHandler.setContextPath("/")
  initSpringContext(contextHandler)

  val handlerList = HandlerCollection()
  handlerList.setHandlers(array<Handler>(resourceHandler, contextHandler))
  server.setHandler(handlerList)

  server.start()
  server.join()
}

