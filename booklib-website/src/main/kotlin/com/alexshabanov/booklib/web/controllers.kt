package com.alexshabanov.booklib.web.controllers

import org.springframework.stereotype.Controller as controller
import org.springframework.web.bind.annotation.RequestMapping as req
import org.springframework.web.bind.annotation.RequestParam as par
import org.springframework.web.bind.annotation.ResponseBody as respBody
import javax.servlet.http.HttpServletResponse
import java.io.OutputStreamWriter
import org.springframework.ui.Model
import org.springframework.web.servlet.ModelAndView
import com.alexshabanov.booklib.service.UserService
import com.alexshabanov.booklib.service.GetUserProfilesResponse
import com.alexshabanov.booklib.service.GetUserProfilesRequest
import java.util.Arrays
import com.alexshabanov.booklib.service.UserProfile

//
// Spring MVC controllers
//

/** Public HTML controller. */
req(array("/g")) controller class PublicController {
  req(array("/index")) fun test(): String = "index"

  req(array("/hello")) fun hello(par("greeting", defaultValue = "Hello") greeting: String): ModelAndView =
      ModelAndView("hello", "greeting", greeting)
}

/** REST API controller. */
req(array("/rest")) controller class RestController(val userService: UserService) {

  req(array("/user")) respBody fun getUserProfiles(par("id") ids: Array<Long>): GetUserProfilesResponse =
      userService.getUserProfiles(GetUserProfilesRequest(userIds = ids.toArrayList())).get()
}
