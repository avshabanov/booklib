package com.truward.ascetic

import com.truward.ascetic.CallResult
import com.truward.ascetic.CallResultCreator
import com.truward.ascetic.Request
import com.truward.ascetic.Response
import com.truward.ascetic.Service
import com.truward.ascetic.Context
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.HashMap

//
// Base support classes
//

class DefaultContext(override val globalRequestId: String = "NONE"): Context {
  val beanRegistry = HashMap<Class<Any>, Any>()

  fun registerBean(bean: Any) {
    beanRegistry.put(bean.javaClass, bean)
  }

  override fun <T> getContextBean(beanClass: Class<T>): T {
    val result = beanRegistry.get(beanClass)
    if (result == null) {
      return null
    }

    if (beanClass.isInstance(result)) {
      return beanClass.cast(result)
    }

    LoggerFactory.getLogger(javaClass).error("Can't cast bean of class=${beanClass} to the specified class, " +
        "gid=${globalRequestId}") // shouldn't happen
    return null
  }

  override fun toString(): String {
    return "<default>"
  }
}

/**
 * Helper class for service implementations.
 *
 * Defines easy-to-use mixins such as <code>resultFrom</code>.
 */
trait ServiceSupport: Service {
  val resultCreator: CallResultCreator

  fun resultFrom<Q: Request, S: Response>(request: Q, calcFun: (Q) -> S): CallResult<S> {
    return resultCreator.create<Q, S>(this, request, calcFun)
  }
}

/** Holds immediate result of a service call. */
class ImmediateCallResult<T: Response>(val result: T): CallResult<T> {
  override fun get(): T = result
}

/** Result creator, that produces the result immediately. */
class ImmediateCallResultCreator(val log: Logger = LoggerFactory.getLogger(javaClass())): CallResultCreator {
  override fun create<Q: Request, S: Response>(caller: Service, request: Q, calcFun: (Q) -> S): CallResult<S> {
    val simpleName = caller.javaClass.getSimpleName()
    val id = request.context.globalRequestId

    // start measuring time
    val startTime = System.currentTimeMillis()
    log.info("[service=${simpleName}][gid=${id}] request=${request}")
    try {
      val result = calcFun(request)
      // stop measuring time
      val timeDelta = System.currentTimeMillis() - startTime
      // log result and execution time
      log.info("[service=${simpleName}][gid=${id}] result=${result}, timeDelta=${timeDelta}")
      return ImmediateCallResult(result)
    } catch (e: Exception) {
      // stop measuring time
      val timeDelta = System.currentTimeMillis() - startTime
      // log exception and execution time
      log.error("[service=${simpleName}][gid=${id}] Error while handling service call, timeDelta=${timeDelta}", e)
      throw e // rethrow an exception, even though it has been logged
    }
  }
}
