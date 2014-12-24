package com.alexshabanov.booklib.service

import com.alexshabanov.booklib.model.BookMeta
import javax.servlet.http.HttpServletResponse
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationContext
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.BeanInitializationException
import org.springframework.context.ConfigurableApplicationContext

/** Encapsulates download service for books. */
trait BookDownloadService {

  fun download(id: Long, response: HttpServletResponse)
}

/** Demo download service - always redirects to sample book. */
class DemoBookDownloadService: BookDownloadService {

  override fun download(id: Long, response: HttpServletResponse) {
    response.sendRedirect("/assets/demo/sample.fb2.zip")
  }
}

/** Download service initializer. */
class BookDownloadServiceFactory: ApplicationContextAware, InitializingBean {
  var context: ApplicationContext? = null
  var service: BookDownloadService? = null

  override fun setApplicationContext(applicationContext: ApplicationContext?) {
    context = applicationContext
  }

  override fun afterPropertiesSet() {
    val c = context
    if (c !is ConfigurableApplicationContext) {
      throw BeanInitializationException("Non-configurable application context, unable to create service") // unlikely
    }

    service = DemoBookDownloadService()
  }

  fun getDownloadService(): BookDownloadService {
    val s = service
    if (s != null) {
      return s
    }
    throw BeanInitializationException("Bean has not been initialized")
  }
}
