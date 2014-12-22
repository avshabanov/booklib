package com.alexshabanov.booklib.web.controllers

import org.springframework.stereotype.Controller as controller
import org.springframework.web.bind.annotation.RequestMapping as req
import org.springframework.web.bind.annotation.RequestParam as par
import org.springframework.web.bind.annotation.PathVariable as pathVar
import org.springframework.web.bind.annotation.ResponseBody as respBody
import javax.servlet.http.HttpServletResponse
import java.io.OutputStreamWriter
import org.springframework.ui.Model
import org.springframework.web.servlet.ModelAndView
import java.util.Arrays
import com.alexshabanov.booklib.service.BookDao
import com.alexshabanov.booklib.model.BookMeta
import com.alexshabanov.booklib.model.NamedValue
import java.util.ArrayList
import com.alexshabanov.booklib.service.BookService

//
// Spring MVC controllers
//

/** Public HTML controller. */
req(array("/g")) controller class PublicController(val bookService: BookService) {
  req(array("/index")) fun index() = ModelAndView("index", "randomBooks", bookService.getRandomBooks())

  req(array("/about")) fun about() = "about"

  req(array("/author/{id}")) fun author(pathVar("id") authorId: Long,
                                        par("nextBookId", required = false) nextBookId: Long?) =
      ModelAndView("author", "pageModel", bookService.getAuthorPageModel(authorId, nextBookId))

  req(array("/genre/{id}")) fun genre(pathVar("id") genreId: Long,
                                      par("nextBookId", required = false) nextBookId: Long?) =
      ModelAndView("genre", "pageModel", bookService.getGenrePageModel(genreId, nextBookId))
}

