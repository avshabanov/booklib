package com.alexshabanov.booklib

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.DefaultServlet
import org.springframework.web.filter.CharacterEncodingFilter
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
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.BeanInitializationException
import org.springframework.context.ApplicationContextInitializer
import org.springframework.core.env.PropertiesPropertySource
import org.springframework.core.io.ResourceLoader
import org.springframework.core.io.support.EncodedResource
import org.springframework.core.io.support.PropertiesLoaderUtils
import org.springframework.web.context.ConfigurableWebApplicationContext
import java.io.IOException
import java.util.*

class AppContextInitializer : ApplicationContextInitializer<ConfigurableWebApplicationContext> {
  override fun initialize(applicationContext: ConfigurableWebApplicationContext?) {
    if (applicationContext !is ResourceLoader) {
      throw IllegalStateException("Application context should be ResourceLoader")
    }

    val coreProperties = applicationContext.getResource("classpath:/settings/core.properties")
    val props = PropertiesLoaderUtils.loadProperties(coreProperties)

    // add override settings
    val overrideSettingsPath = System.getProperty("booklib.settings.path");
    if (overrideSettingsPath != null) {
      PropertiesLoaderUtils.fillProperties(props, applicationContext.getResource(overrideSettingsPath))
    } else {
      throw IllegalStateException("No path registered at booklib.settings.path")
    }

    LoggerFactory.getLogger(javaClass).debug("AppContextInitializer props={}", props)

    val ps = PropertiesPropertySource("profile", props)
    applicationContext.getEnvironment()?.getPropertySources()?.addFirst(ps)
  }
}

private fun initSpringContext(context: ServletContextHandler) {
  context.setInitParameter("contextConfigLocation", "classpath:/spring/service-context.xml")
  val appContextInitializerLocation = javaClass<AppContextInitializer>().getName()
  context.setInitParameter("contextInitializerClasses", appContextInitializerLocation)

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
  server.setSendServerVersion(false)

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

