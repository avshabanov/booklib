package com.alexshabanov.booklib.web

import org.springframework.stereotype.Controller as controller
import org.springframework.web.bind.annotation.RequestMapping as req
import org.springframework.web.bind.annotation.RequestParam as par
import org.springframework.web.bind.annotation.PathVariable as pathVar
import org.springframework.web.bind.annotation.ResponseBody as respBody
import org.springframework.web.servlet.ModelAndView
import com.alexshabanov.booklib.service.BookService
import javax.servlet.http.HttpServletResponse
import com.alexshabanov.booklib.service.BookDownloadService
import com.truward.time.UtcTime
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.authentication.InternalAuthenticationServiceException
import com.alexshabanov.booklib.model.UserAccount
import com.alexshabanov.booklib.model.FavoriteKind
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

//
// Spring MVC controllers
//

val MAX_NAME_HINT_LENGTH = 3

/** Base HTML controller. */
org.springframework.web.bind.annotation.RequestMapping("/g") org.springframework.stereotype.Controller open class StandardHtmlController {
  ModelAttribute("userAccount") fun getUserAccount(): UserAccount? {
    val auth = SecurityContextHolder.getContext().getAuthentication()
    if (auth != null) {
      val details = auth.getPrincipal()
      if (details is UserAccount) {
        return details
      } else {
        throw InternalAuthenticationServiceException("Unexpected principal type: ${details}")
      }
    }

    return null
  }

  fun getUserId(): Long {
    val account = getUserAccount()
    if (account != null) {
      return account.id
    }
    throw InternalAuthenticationServiceException("UserAccount has not been found")
  }
}

/** Generic pages controller. */
class GenericController(val bookService: BookService): StandardHtmlController() {

  org.springframework.web.bind.annotation.RequestMapping("/index") fun index(): ModelAndView {
    val userId = getUserId()
    val favs = bookService.userService.getFavorites(userId)

    // filter entry ids
    val favBookIds = favs.filter { it -> it.kind == FavoriteKind.BOOK }
    val favAuthorIds = favs.filter { it -> it.kind == FavoriteKind.AUTHOR }

    // fetch favorite books and authors
    val favBooks = favBookIds.map { it -> bookService.getBookById(userId, it.entityId) }
    val favAuthors = favAuthorIds.map { it -> bookService.getAuthorById(it.entityId) }

    return ModelAndView("index", mapOf(
        Pair("favBooks", favBooks),
        Pair("favAuthors", favAuthors)))
  }

  org.springframework.web.bind.annotation.RequestMapping("/about") fun about() = "about"

  org.springframework.web.bind.annotation.RequestMapping("/login") fun login(org.springframework.web.bind.annotation.RequestParam("error", required = false) loginError: String?): ModelAndView {
    return ModelAndView("login", mapOf(Pair("loginError", loginError), Pair("currentTime", UtcTime.now())))
  }
}

/** Book-specific pages */
class BookController(val bookService: BookService, val downloadService: BookDownloadService): StandardHtmlController() {

  org.springframework.web.bind.annotation.RequestMapping("/book/{id}") fun book(org.springframework.web.bind.annotation.PathVariable("id") bookId: Long) =
      ModelAndView("book", "book", bookService.getBookById(getUserId(), bookId))

  org.springframework.web.bind.annotation.RequestMapping("/book/{id}/download") fun downloadBook(org.springframework.web.bind.annotation.PathVariable("id") bookId: Long, response: HttpServletResponse) {
    downloadService.download(bookId, response)
  }
}

/** Genre-specific pages */
class GenreController(val bookService: BookService): StandardHtmlController() {

  org.springframework.web.bind.annotation.RequestMapping("/genre/{id}") fun genre(org.springframework.web.bind.annotation.PathVariable("id") genreId: Long,
                                      org.springframework.web.bind.annotation.RequestParam("startBookId", required = false) startBookId: Long?) =
      ModelAndView("genre", mapOf(
          Pair("curBookId", startBookId),
          Pair("pageModel", bookService.getGenrePageModel(getUserId(), genreId, startBookId))))

  org.springframework.web.bind.annotation.RequestMapping("/genres") fun genres() = ModelAndView("genre-list", "genreList", bookService.getGenres())
}

/** Author-specific pages */
class AuthorController(val bookService: BookService): StandardHtmlController() {

  org.springframework.web.bind.annotation.RequestMapping("/author/{id}") fun author(org.springframework.web.bind.annotation.PathVariable("id") authorId: Long,
                                        org.springframework.web.bind.annotation.RequestParam("startBookId", required = false) startBookId: Long?) =
      ModelAndView("author", mapOf(
          Pair("curBookId", startBookId),
          Pair("pageModel", bookService.getAuthorPageModel(getUserId(), authorId, startBookId))))

  org.springframework.web.bind.annotation.RequestMapping("/authors") fun authors(org.springframework.web.bind.annotation.RequestParam("namePrefix", required = false) namePrefix: String?,
                                     org.springframework.web.bind.annotation.RequestParam("startName", required = false) startName: String?): ModelAndView {
    if (namePrefix != null && namePrefix.length() >= MAX_NAME_HINT_LENGTH) {
      // author full name list
      val limit = Integer.MAX_VALUE // TODO: support pagination for author list?
      return ModelAndView("author-list", "authorList", bookService.getAuthorsByNamePrefix(namePrefix, startName, limit))
    }

    // name hint
    return ModelAndView("author-names", "prefixList", bookService.getAuthorNameHint(namePrefix))
  }

  org.springframework.web.bind.annotation.RequestMapping(value = "/author/rest/favorite/toggle", method = arrayOf(RequestMethod.POST))
  ResponseBody
  fun changeFavStatus(
      org.springframework.web.bind.annotation.RequestParam("kind") kind: FavoriteKind, org.springframework.web.bind.annotation.RequestParam("entityId") entityId: Long): Boolean {
    val userId = getUserId()
    if (bookService.userService.isFavorite(userId, kind, entityId)) {
      bookService.userService.resetFavorite(userId, kind, entityId)
      return false
    } else {
      bookService.userService.setFavorite(userId, kind, entityId)
      return true
    }
  }
}

/* Language-specific pages */
class LangController(val bookService: BookService): StandardHtmlController() {

  org.springframework.web.bind.annotation.RequestMapping("/language/{id}") fun language(org.springframework.web.bind.annotation.PathVariable("id") languageId: Long,
                                            org.springframework.web.bind.annotation.RequestParam("startBookId", required = false) startBookId: Long?) =
      ModelAndView("language", mapOf(
          Pair("curBookId", startBookId),
          Pair("pageModel", bookService.getLanguagePageModel(getUserId(), languageId, startBookId))))

  org.springframework.web.bind.annotation.RequestMapping("/languages") fun languages() = ModelAndView("language-list", "langList", bookService.getLanguages())
}
