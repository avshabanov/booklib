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
import com.alexshabanov.booklib.util.ArgParser
import org.springframework.web.filter.DelegatingFilterProxy
import org.eclipse.jetty.servlet.FilterHolder


private fun initSpringContext(context: ServletContextHandler) {
  context.setInitParameter("contextConfigLocation", "classpath:/spring/service-context.xml,classpath:/spring/security-context.xml")

  // Enforce UTF-8 encoding
  val encFilterHolder = context.addFilter(javaClass<CharacterEncodingFilter>(), "/*", EnumSet.allOf(javaClass<DispatcherType>()))
  encFilterHolder.setInitParameter("encoding", "UTF-8")
  encFilterHolder.setInitParameter("forceEncoding", "true")

  // Spring security
  val delegatingFilterHolder = FilterHolder(javaClass<DelegatingFilterProxy>())
  delegatingFilterHolder.setName("springSecurityFilterChain")
  context.addFilter(delegatingFilterHolder, "/*", EnumSet.allOf(javaClass<DispatcherType>()))

  // Spring loader context
  context.addEventListener(ContextLoaderListener())

  // Main spring servlet
  // NOTE: /j_spring_security_check is associated with dispatcher servlet, but it is actually handled by DelegatingFilterProxy!
  val ds = context.addServlet(javaClass<DispatcherServlet>(), "/g/*,/rest/*,/j_spring_security_check")
  ds.setInitParameter("contextConfigLocation", "classpath:/spring/webmvc-context.xml")
}

private fun startServer(port: Int, configPath: String) {
  System.setProperty("booklib.settings.path", configPath)

  val server = Server(port)

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

/** Entry point. */
fun main(args: Array<String>) {
  val argParser = ArgParser(args)
  val parseResult = argParser.parse()
  if (parseResult != 0) {
    System.exit(parseResult)
  }

  startServer(argParser.port, argParser.configPath)
}

