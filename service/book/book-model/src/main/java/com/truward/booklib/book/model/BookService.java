package com.truward.booklib.book.model;

import org.springframework.web.bind.annotation.*;

/**
 * @author Alexander Shabanov
 */
public interface BookService {

  @RequestMapping(value = "/books/page", method = RequestMethod.PUT)
  @ResponseBody
  BookModel.BookPageIds savePage(@RequestBody BookModel.BookPageData request);

  @RequestMapping(value = "/books/page", method = RequestMethod.DELETE)
  @ResponseBody
  BookModel.BookPageIds delete(@RequestBody BookModel.BookPageIds request);

  @RequestMapping(value = "/books/page/fetch", method = RequestMethod.POST)
  @ResponseBody
  BookModel.BookPageData getPage(@RequestBody BookModel.BookPageIds request);

  @RequestMapping(value = "/genres/page/query", method = RequestMethod.POST)
  @ResponseBody
  BookModel.BookPageData queryBookPage(@RequestBody BookModel.BookPageQuery query);

  @RequestMapping(value = "/genres", method = RequestMethod.GET)
  @ResponseBody
  BookModel.GenreList getGenres();

  @RequestMapping(value = "/languages", method = RequestMethod.GET)
  @ResponseBody
  BookModel.LanguageList getLanguages();
}
