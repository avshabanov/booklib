package com.truward.booklib.book.server.controller;

import com.truward.booklib.book.model.BookModel;
import com.truward.booklib.book.model.BookRestService;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nonnull;


/**
 * @author Alexander Shabanov
 */
@Controller
@RequestMapping("/rest/book")
public final class BookRestController implements BookRestService {
  private final BookRestService bookService;

  public BookRestController(@Nonnull BookRestService bookService) {
    Assert.notNull(bookService, "bookService");
    this.bookService = bookService;
  }

  @Override
  public BookModel.BookPageIds savePage(@RequestBody BookModel.BookPageData request) {
    return bookService.savePage(request);
  }

  @Override
  public BookModel.BookPageIds delete(@RequestBody BookModel.BookPageIds request) {
    return bookService.delete(request);
  }

  @Override
  public BookModel.BookList queryBooks(@RequestBody BookModel.BookPageQuery query) {
    return bookService.queryBooks(query);
  }

  @Override
  public BookModel.BookPageData getPage(@RequestBody BookModel.BookPageIdsRequest request) {
    return bookService.getPage(request);
  }

  @Override
  public BookModel.NamedValueList getGenres() {
    return bookService.getGenres();
  }

  @Override
  public BookModel.NamedValueList getLanguages() {
    return bookService.getLanguages();
  }

  @Override
  public BookModel.NamedValueList queryPersons(@RequestBody BookModel.PersonListQuery query) {
    return bookService.queryPersons(query);
  }

  @Override
  public BookModel.PersonNameHints getPersonHints(@RequestParam(value = "prefix", required = false) String prefix) {
    return bookService.getPersonHints(prefix);
  }

  @Override
  public BookModel.NamedValueList querySeries(@RequestBody BookModel.SeriesQuery query) {
    return bookService.querySeries(query);
  }
}
