package com.alexshabanov.booklib.web.controllers

import org.springframework.stereotype.Controller as controller
import org.springframework.web.bind.annotation.RequestMapping as req
import org.springframework.web.bind.annotation.RequestParam as par
import org.springframework.web.bind.annotation.PathVariable as pathVar
import org.springframework.web.bind.annotation.ResponseBody as respBody
import org.springframework.web.servlet.ModelAndView
import com.alexshabanov.booklib.service.BookService
import com.alexshabanov.booklib.service.DEFAULT_LIMIT
import javax.servlet.http.HttpServletResponse
import com.alexshabanov.booklib.service.BookDownloadService

//
// Spring MVC controllers
//

val MAX_NAME_HINT_LENGTH = 3

/** Base HTML controller. */
req(array("/g")) controller open class StandardHtmlController

/** Generic pages controller. */
class GenericController(val bookService: BookService): StandardHtmlController() {

  req(array("/index")) fun index() = ModelAndView("index", "randomBooks", bookService.getRandomBooks())

  req(array("/about")) fun about() = "about"
}

/** Book-specific pages */
class BookController(val bookService: BookService, val downloadService: BookDownloadService): StandardHtmlController() {

  req(array("/book/{id}")) fun book(pathVar("id") bookId: Long) =
      ModelAndView("book", "book", bookService.getBookById(bookId))

  req(array("/book/{id}/download")) fun downloadBook(pathVar("id") bookId: Long, response: HttpServletResponse) {
    downloadService.download(bookId, response)
  }
}

/** Genre-specific pages */
class GenreController(val bookService: BookService): StandardHtmlController() {

  req(array("/genre/{id}")) fun genre(pathVar("id") genreId: Long,
                                      par("startBookId", required = false) startBookId: Long?) =
      ModelAndView("genre", mapOf(
          Pair("curBookId", startBookId),
          Pair("pageModel", bookService.getGenrePageModel(genreId, startBookId))))

  req(array("/genres")) fun genres() = ModelAndView("genre-list", "genreList", bookService.getGenres())
}

/** Author-specific pages */
class AuthorController(val bookService: BookService): StandardHtmlController() {

  req(array("/author/{id}")) fun author(pathVar("id") authorId: Long,
                                        par("startBookId", required = false) startBookId: Long?) =
      ModelAndView("author", mapOf(
          Pair("curBookId", startBookId),
          Pair("pageModel", bookService.getAuthorPageModel(authorId, startBookId))))

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