package com.truward.booklib.website.controller;

import com.truward.book.model.BookModel;
import com.truward.book.model.BookRestService;
import com.truward.booklib.model.AjaxRestService;
import com.truward.booklib.model.Booklib;
import com.truward.extid.model.GroupsRestService;
import com.truward.extid.model.IdPairsRestService;
import com.truward.extid.model.TypesRestService;
import com.truward.p13n.model.FavoritesRestService;
import com.truward.p13n.model.P13n;
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
  private final TypesRestService typesRestService;
  private final GroupsRestService groupsRestService;
  private final IdPairsRestService idPairsRestService;

  private final List<Long> favBooks = Arrays.asList(1L, 4L, 5L);
  private final List<Long> favPersons = Arrays.asList(1L, 2L);

  private final int bookType;
  private final int personType;

  public AjaxController(BookRestService bookService,
                        FavoritesRestService favoritesRestService,
                        TypesRestService typesRestService,
                        GroupsRestService groupsRestService,
                        IdPairsRestService idPairsRestService) {
    this.bookService = bookService;
    this.favoritesRestService = favoritesRestService;
    this.typesRestService = typesRestService;
    this.groupsRestService = groupsRestService;
    this.idPairsRestService = idPairsRestService;

    // TODO: fetch from extid service in @PostConstruct (or lazy init method)
    this.bookType = 10; // TODO: remove hardcoded value from extid-fixture.sql
    this.personType = 11; // TODO: remove hardcoded value from extid-fixture.sql
  }

  //
  // P13n API
  //

  @RequestMapping(value = "/p13n/favorites", method = RequestMethod.GET)
  @ResponseBody
  public Booklib.GetFavoritesResponse getFavorites() {
    if (favoritesRestService == null) {
      return Booklib.GetFavoritesResponse.newBuilder()
          .setFavorites(Booklib.GetFavoritesResponse.Favorites.newBuilder()
              .addAllBookIds(favBooks)
              .addAllPersonIds(favPersons)
              .build())
          .build();
    }

    final P13n.GetAllFavoritesResponse p13nResp = favoritesRestService.getAllFavorites(P13n.GetAllFavoritesRequest.newBuilder()
        .setUserId(getUserId())
        .addTypes(bookType).addTypes(personType)
        .build());

    final Booklib.GetFavoritesResponse.Favorites.Builder builder = Booklib.GetFavoritesResponse.Favorites.newBuilder();

    for (final P13n.GetAllFavoritesResponse.Entry e : p13nResp.getEntriesList()) {
      if (e.getType() == bookType) {
        builder.addAllBookIds(e.getIdsList());
      } else if (e.getType() == personType) {
        builder.addAllPersonIds(e.getIdsList());
      }
    }

    return Booklib.GetFavoritesResponse.newBuilder().setFavorites(builder.build()).build();
  }

  //
  // Book API
  //

  @RequestMapping(value = "/books/page", method = RequestMethod.PUT)
  @ResponseBody
  public Booklib.BookPageIds savePage(@RequestBody BookModel.BookPageData request) {
    if (bookService == null) {
      return Booklib.BookPageIds.newBuilder().build();
    }

    return from(bookService.savePage(request));
  }

  @RequestMapping(value = "/books/page", method = RequestMethod.DELETE)
  @ResponseBody
  public Booklib.BookPageIds delete(@RequestBody BookModel.BookPageIds request) {
    if (bookService == null) {
      return Booklib.BookPageIds.newBuilder().build();
    }

    return from(bookService.delete(request));
  }


  @RequestMapping(value = "/books/query", method = RequestMethod.POST)
  @ResponseBody
  public BookModel.BookList queryBooks(@RequestBody BookModel.BookPageQuery query) {
    if (bookService == null) {
      return BookModel.BookList.newBuilder().build();
    }

    return bookService.queryBooks(query);
  }

  @RequestMapping(value = "/books/page/fetch", method = RequestMethod.POST)
  @ResponseBody
  public BookModel.BookPageData getPage(@RequestBody BookModel.BookPageIdsRequest request) {
    if (bookService == null) {
      return BookModel.BookPageData.newBuilder().build();
    }

    return bookService.getPage(request);
  }

  @RequestMapping(value = "/genres", method = RequestMethod.GET)
  @ResponseBody
  public Booklib.NamedValueList getGenres() {
    if (bookService == null) {
      return Booklib.NamedValueList.newBuilder().build();
    }

    return from(bookService.getGenres());
  }

  @RequestMapping(value = "/languages", method = RequestMethod.GET)
  @ResponseBody
  public Booklib.NamedValueList getLanguages() {
    if (bookService == null) {
      return Booklib.NamedValueList.newBuilder().build();
    }

    return from(bookService.getLanguages());
  }

  @RequestMapping(value = "/persons/query", method = RequestMethod.POST)
  public Booklib.NamedValueList queryPersons(@RequestBody BookModel.PersonListQuery query) {
    if (bookService == null) {
      return Booklib.NamedValueList.newBuilder().build();
    }

    return from(bookService.queryPersons(query));
  }

  @RequestMapping(value = "/persons/hint", method = RequestMethod.POST)
  public BookModel.PersonNameHints getPersonHints(@RequestParam(value = "prefix", required = false) String prefix) {
    if (bookService == null) {
      return BookModel.PersonNameHints.newBuilder().build();
    }

    return bookService.getPersonHints(prefix);
  }

  @RequestMapping(value = "/series/query", method = RequestMethod.POST)
  public Booklib.NamedValueList querySeries(@RequestBody BookModel.SeriesQuery query) {
    if (bookService == null) {
      return Booklib.NamedValueList.newBuilder().build();
    }

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

  private long getUserId() {
    return 120L; // TODO: fetch from session info
  }
}
