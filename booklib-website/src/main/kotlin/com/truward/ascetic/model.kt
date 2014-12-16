package com.truward.ascetic

/** Caller context. */
trait Context {
  val globalRequestId : String

  fun getContextBean<T>(beanClass: Class<T>): T
}

/** Marker interface for service call request DTOs. */
trait Request {
  /** Each request carries a context in which it has been created. It is required for making nested reporting. */
  val context: Context
}

/** Marker interface for service call result DTOs. */
trait Response

/** Marker interface for services. */
trait Service

/** Model for service response. */
trait CallResult<T: Response> {
  fun get(): T
}

/** Encapsulates the way responses will be created. */
trait CallResultCreator {
  fun create<Q: Request, S: Response>(caller: Service, request: Q, calcFun: (Q) -> S): CallResult<S>
}
