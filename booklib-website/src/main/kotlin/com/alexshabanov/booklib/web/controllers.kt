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
import com.alexshabanov.booklib.service.Book
import com.alexshabanov.booklib.service.DEFAULT_LIMIT

//
// Spring MVC controllers
//

val MAX_NAME_HINT_LENGTH = 3

/** Public HTML controller. */
req(array("/g")) controller class PublicController(val bookService: BookService) {

  req(array("/index")) fun index() = ModelAndView("index", "randomBooks", bookService.getRandomBooks())

  req(array("/about")) fun about() = "about"
}

/** Genre-specific pages */
req(array("/g")) controller class GenreController(val bookService: BookService) {

  req(array("/genre/{id}")) fun genre(pathVar("id") genreId: Long,
                                      par("startBookId", required = false) startBookId: Long?) =
      ModelAndView("genre", "pageModel", bookService.getGenrePageModel(genreId, startBookId))

  req(array("/genres")) fun genres() = ModelAndView("genre-list", "genreList", bookService.getGenres())
}

/** Author-specific pages */
req(array("/g")) controller class AuthorController(val bookService: BookService) {

  req(array("/author/{id}")) fun author(pathVar("id") authorId: Long,
                                        par("startBookId", required = false) startBookId: Long?) =
      ModelAndView("author", "pageModel", bookService.getAuthorPageModel(authorId, startBookId))

  req(array("/authors")) fun authors(par("namePrefix", required = false) namePrefix: String?,
                                     par("startName", required = false) startName: String?): ModelAndView {
    if (namePrefix != null && namePrefix.length() >= MAX_NAME_HINT_LENGTH) {
      // author full name list
      val limit = Integer.MAX_VALUE // TODO: support pagination for author list?
      return ModelAndView("author-list", "authorList", bookService.getAuthorsByNamePrefix(namePrefix, startName, limit))
    }

    // name hint
    return ModelAndView("author-names", "prefixList", bookService.getAuthorNameHint(namePrefix))
  }
}