package com.truward.booklib.website.controller;

import com.truward.booklib.book.model.BookModel;
import com.truward.booklib.book.model.BookRestService;
import com.truward.booklib.model.AjaxRestService;
import com.truward.booklib.model.Booklib;
import com.truward.p13n.model.FavoritesRestService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * @author Alexander Shabanov
 */
@Controller
@RequestMapping("/rest/ajax/")
public final class AjaxController implements AjaxRestService {
  private final BookRestService bookService;
  private final FavoritesRestService favoritesRestService;

  private final List<Long> favBooks = Arrays.asList(1L, 4L, 5L);
  private final List<Long> favPersons = Arrays.asList(1L, 2L);

  public AjaxController(BookRestService bookService) {
    this.bookService = bookService; this.favoritesRestService = null; // TODO: init
  }

  //
  // P13n API
  //

  @RequestMapping(value = "/p13n/favorites", method = RequestMethod.GET)
  @ResponseBody
  public Booklib.GetFavoritesResponse getFavorites() {
    // TODO: p13n favs
    return Booklib.GetFavoritesResponse.newBuilder()
        .setFavorites(Booklib.GetFavoritesResponse.Favorites.newBuilder()
            .addAllBookIds(favBooks)
            .addAllPersonIds(favPersons)
            .build())
        .build();
  }

  //
  // Book API
  //

  @RequestMapping(value = "/books/page", method = RequestMethod.PUT)
  @ResponseBody
  public Booklib.BookPageIds savePage(@RequestBody BookModel.BookPageData request) {
    return from(bookService.savePage(request));
  }

  @RequestMapping(value = "/books/page", method = RequestMethod.DELETE)
  @ResponseBody
  public Booklib.BookPageIds delete(@RequestBody BookModel.BookPageIds request) {
    return from(bookService.delete(request));
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
  public Booklib.NamedValueList getGenres() {
    return from(bookService.getGenres());
  }

  @RequestMapping(value = "/languages", method = RequestMethod.GET)
  @ResponseBody
  public Booklib.NamedValueList getLanguages() {
    return from(bookService.getLanguages());
  }

  @RequestMapping(value = "/persons/query", method = RequestMethod.POST)
  public Booklib.NamedValueList queryPersons(@RequestBody BookModel.PersonListQuery query) {
    return from(bookService.queryPersons(query));
  }

  @RequestMapping(value = "/persons/hint", method = RequestMethod.POST)
  public BookModel.PersonNameHints getPersonHints(@RequestParam(value = "prefix", required = false) String prefix) {
    return bookService.getPersonHints(prefix);
  }

  @RequestMapping(value = "/series/query", method = RequestMethod.POST)
  public Booklib.NamedValueList querySeries(@RequestBody BookModel.SeriesQuery query) {
    return from(bookService.querySeries(query));
  }

  //
  // Private
  //

  private static Booklib.NamedValueList from(BookModel.NamedValueList value) {
    final Booklib.NamedValueList.Builder builder = Booklib.NamedValueList.newBuilder();
    for (final BookModel.NamedValue v : value.getValuesList()) {
      builder.addValues(from(v));
    }
    return builder.build();
  }

  private static Booklib.NamedValue from(BookModel.NamedValue value) {
    return Booklib.NamedValue.newBuilder()
        .setId(value.getId())
        .setName(value.getName())
        .build();
  }

  private static Booklib.BookPageIds from(BookModel.BookPageIds value) {
    return Booklib.BookPageIds.newBuilder()
        .addAllGenreIds(value.getGenreIdsList())
        .addAllPersonIds(value.getPersonIdsList())
        .addAllOriginIds(value.getOriginIdsList())
        .addAllLanguageIds(value.getLanguageIdsList())
        .addAllBookIds(value.getBookIdsList())
        .addAllSeriesIds(value.getSeriesIdsList())
        .build();
  }
}
