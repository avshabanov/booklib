package com.truward.booklib.book.model;

import org.springframework.web.bind.annotation.*;

/**
 * @author Alexander Shabanov
 */
public interface BookRestService {

  @RequestMapping(value = "/books/page", method = RequestMethod.PUT)
  @ResponseBody
  BookModel.BookPageIds savePage(@RequestBody BookModel.BookPageData request);

  @RequestMapping(value = "/books/page", method = RequestMethod.DELETE)
  @ResponseBody
  BookModel.BookPageIds delete(@RequestBody BookModel.BookPageIds request);

  @RequestMapping(value = "/books/query", method = RequestMethod.POST)
  @ResponseBody
  BookModel.BookList queryBooks(@RequestBody BookModel.BookPageQuery query);

  @RequestMapping(value = "/books/page/fetch", method = RequestMethod.POST)
  @ResponseBody
  BookModel.BookPageData getPage(@RequestBody BookModel.BookPageIdsRequest request);

  @RequestMapping(value = "/genres", method = RequestMethod.GET)
  @ResponseBody
  BookModel.NamedValueList getGenres();

  @RequestMapping(value = "/languages", method = RequestMethod.GET)
  @ResponseBody
  BookModel.NamedValueList getLanguages();

  @RequestMapping(value = "/persons/query", method = RequestMethod.POST)
  BookModel.NamedValueList queryPersons(@RequestBody BookModel.PersonListQuery query);

  @RequestMapping(value = "/persons/hint", method = RequestMethod.POST)
  BookModel.PersonNameHints getPersonHints(@RequestParam(value = "prefix", required = false) String prefix);
}
