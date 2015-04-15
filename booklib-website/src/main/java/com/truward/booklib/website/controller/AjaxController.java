package com.truward.booklib.website.controller;

import com.truward.booklib.book.model.BookModel;
import com.truward.booklib.book.model.BookRestService;
import com.truward.booklib.model.BooklibModel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * @author Alexander Shabanov
 */
@Controller
@RequestMapping("/rest/ajax/")
public final class AjaxController {
  private final BookRestService bookService;

  private final List<Long> favBooks = Arrays.asList(1L, 4L, 5L);
  private final List<Long> favPersons = Arrays.asList(1L, 2L);

  public AjaxController(BookRestService bookService) {
    this.bookService = bookService;
  }

  //
  // P13n API
  //

  @RequestMapping(value = "/p13n/favorites", method = RequestMethod.GET)
  @ResponseBody
  public BooklibModel.GetFavoritesResponse getFavorites() {
    return BooklibModel.GetFavoritesResponse.newBuilder()
        .setFavorites(BooklibModel.GetFavoritesResponse.Favorites.newBuilder()
            .addAllBookIds(favBooks)
            .addAllPersonIds(favPersons)
            .build())
        .build();
  }

//  @RequestMapping(value = "/p13n/favorites", method = RequestMethod.PUT)
//  @ResponseBody
//  public BookModel.BookPageIds savePage() {
//    return bookService.savePage(request);
//  }

  //
  // Book API
  //

  @RequestMapping(value = "/books/page", method = RequestMethod.PUT)
  @ResponseBody
  public BookModel.BookPageIds savePage(@RequestBody BookModel.BookPageData request) {
    return bookService.savePage(request);
  }

  @RequestMapping(value = "/books/page", method = RequestMethod.DELETE)
  @ResponseBody
  public BookModel.BookPageIds delete(@RequestBody BookModel.BookPageIds request) {
    return bookService.delete(request);
  }

  @RequestMapping(value = "/books/query", method = RequestMethod.POST)
  @ResponseBody
  public BookModel.BookList queryBooks(@RequestBody BookModel.BookPageQuery query) {
    return bookService.queryBooks(query);
  }

  @RequestMapping(value = "/books/page/fetch", method = RequestMethod.POST)
  @ResponseBody
  public BookModel.BookPageData getPage(@RequestBody BookModel.BookPageIdsRequest request) {
    return bookService.getPage(request);
  }

  @RequestMapping(value = "/genres", method = RequestMethod.GET)
  @ResponseBody
  public BookModel.NamedValueList getGenres() {
    return bookService.getGenres();
  }

  @RequestMapping(value = "/languages", method = RequestMethod.GET)
  @ResponseBody
  public BookModel.NamedValueList getLanguages() {
    return bookService.getLanguages();
  }

  @RequestMapping(value = "/persons/query", method = RequestMethod.POST)
  public BookModel.NamedValueList queryPersons(@RequestBody BookModel.PersonListQuery query) {
    return bookService.queryPersons(query);
  }

  @RequestMapping(value = "/persons/hint", method = RequestMethod.POST)
  public BookModel.PersonNameHints getPersonHints(@RequestParam(value = "prefix", required = false) String prefix) {
    return bookService.getPersonHints(prefix);
  }

  @RequestMapping(value = "/series/query", method = RequestMethod.POST)
  public BookModel.NamedValueList querySeries(@RequestBody BookModel.SeriesQuery query) {
    return bookService.querySeries(query);
  }
}
