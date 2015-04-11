package com.truward.booklib.book.server.test;

import com.truward.booklib.book.model.BookModel;
import com.truward.booklib.book.model.BookRestService;
import com.truward.time.UtcTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Alexander Shabanov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/spring/BookServiceTest-context.xml")
@Transactional
public final class BookServiceTest {
  @Resource BookRestService bookService;

  @Test
  public void shouldFindAndDeleteAll() {
    // Given
    final BookModel.BookPageQuery queryAll = BookModel.BookPageQuery.newBuilder()
        .setSortType(BookModel.BookPageQuery.SortType.ID).setLimit(64).build();
    final BookModel.BookList list = bookService.queryBooks(queryAll);

    final BookModel.BookPageData page = bookService.getPage(BookModel.BookPageIdsRequest.newBuilder()
        .setPageIds(BookModel.BookPageIds.newBuilder().addAllBookIds(list.getBookIdsList()).build())
        .setFetchBookDependencies(true)
        .build());

    final BookModel.BookPageIds allIds = toPageIds(page);

    // When:
    bookService.delete(allIds);

    // Then:
    assertEquals(0, bookService.getGenres().getValuesCount());
    assertEquals(0, bookService.getLanguages().getValuesCount());
    assertEquals(2, bookService.getPersonHints(null).getNamePartsCount());
    assertEquals(0, bookService.queryBooks(queryAll).getBookIdsCount());
    assertEquals(0, bookService.querySeries(BookModel.SeriesQuery.newBuilder().setLimit(2).build()).getValuesCount());
  }

  @Test
  public void shouldQueryPersons() {
    // Given:
    final BookModel.PersonListQuery query = BookModel.PersonListQuery.newBuilder()
        .setLimit(64)
        .build();

    // When:
    final BookModel.NamedValueList persons = bookService.queryPersons(query);

    // Then:
    assertEquals(8, persons.getValuesCount());
  }

  @Test
  public void shouldGetAllGenres() {
    // Given:

    // When:
    final BookModel.NamedValueList list = bookService.getGenres();

    // Then:
    assertTrue(list.getValuesCount() > 0);
  }

  @Test
  public void shouldSavePage() {
    // Given:
    final String genre = "fantastische";
    final String series = "The Noon";
    final String extType = "google";
    final String author = "bob";
    final String lang = "cn";
    final String origin = "origin";

    // When:
    final BookModel.BookPageIds ids = bookService.savePage(BookModel.BookPageData.newBuilder()
        .addSeries(BookModel.NamedValue.newBuilder().setName(series))
        .addGenres(BookModel.NamedValue.newBuilder().setName(genre))
        .addExternalBookTypes(BookModel.NamedValue.newBuilder().setName(extType))
        .addPersons(BookModel.NamedValue.newBuilder().setName(author))
        .addLanguages(BookModel.NamedValue.newBuilder().setName(lang))
        .addOrigins(BookModel.NamedValue.newBuilder().setName(origin))
        .build());

    // Then:
    final BookModel.BookPageData pageData = bookService.getPage(BookModel.BookPageIdsRequest.newBuilder()
        .setPageIds(ids).setFetchBookDependencies(false).build());

    assertEquals(1, pageData.getGenresCount());
    assertEquals(genre, pageData.getGenres(0).getName());

    assertEquals(1, pageData.getSeriesCount());
    assertEquals(series, pageData.getSeries(0).getName());

    assertEquals(1, pageData.getExternalBookTypesCount());
    assertEquals(extType, pageData.getExternalBookTypes(0).getName());

    assertEquals(1, pageData.getPersonsCount());
    assertEquals(author, pageData.getPersons(0).getName());

    assertEquals(1, pageData.getLanguagesCount());
    assertEquals(lang, pageData.getLanguages(0).getName());

    assertEquals(1, pageData.getOriginsCount());
    assertEquals(origin, pageData.getOrigins(0).getName());

    // [2] Given:
    final BookModel.BookMeta book = BookModel.BookMeta.newBuilder()
        .setAddDate(UtcTime.days(10).getTime())
        .setFileSize(12)
        .setTitle("title")
        .setLangId(pageData.getLanguages(0).getId())
        .setOriginId(pageData.getOrigins(0).getId())
        .addPersonRelations(BookModel.PersonRelation.newBuilder().setId(pageData.getPersons(0).getId())
            .setRelation(BookModel.PersonRelation.Type.AUTHOR))
        .addExternalIds(BookModel.ExternalBookId.newBuilder().setId("externalId")
            .setTypeId(pageData.getExternalBookTypes(0).getId()))
        .addGenreIds(pageData.getGenres(0).getId())
        .addSeriesPos(BookModel.SeriesPos.newBuilder().setId(pageData.getSeries(0).getId()).setPos(5))
        .build();

    // [2] When:
    final BookModel.BookPageIds ids2 = bookService.savePage(BookModel.BookPageData.newBuilder().addBooks(book).build());

    // [2] Then:
    assertEquals(1, ids2.getBookIdsCount());
    final long bookId = ids2.getBookIds(0);
    final BookModel.BookPageIdsRequest bookPageIdsRequest = BookModel.BookPageIdsRequest.newBuilder()
        .setFetchBookDependencies(true).setPageIds(BookModel.BookPageIds.newBuilder().addBookIds(bookId)).build();
    final BookModel.BookPageData pageData2 = bookService.getPage(bookPageIdsRequest);

    assertEquals(BookModel.BookPageData.newBuilder(pageData)
        .addBooks(BookModel.BookMeta.newBuilder(book).setId(bookId)).build(), pageData2);

    // [3] Given:
    final BookModel.BookPageData pageData3 = BookModel.BookPageData.newBuilder()
        .addGenres(updateSingle(pageData2.getGenresList(), "newGenre"))
        .addLanguages(updateSingle(pageData2.getLanguagesList(), "newLang"))
        .addPersons(updateSingle(pageData2.getPersonsList(), "newPerson"))
        .addSeries(updateSingle(pageData2.getSeriesList(), "newSeries"))
        .addExternalBookTypes(updateSingle(pageData2.getExternalBookTypesList(), "newBookType"))
        .addOrigins(updateSingle(pageData2.getOriginsList(), "newOrigin"))
        .addBooks(BookModel.BookMeta.newBuilder(book)
            .setId(bookId)
            .setTitle("newTitle")
            .setFileSize(456)
            .setAddDate(UtcTime.days(456).getTime())
            .build())
        .build();

    // [3] When:
    final BookModel.BookPageIds ids3 = bookService.savePage(pageData3);

    // [3] Then:
    assertEquals(BookModel.BookPageIds.newBuilder(ids).addBookIds(bookId).build(), ids3);
    assertEquals(pageData3, bookService.getPage(bookPageIdsRequest));
  }

  //
  // Private
  //

  @Nonnull static BookModel.NamedValue updateSingle(@Nonnull List<BookModel.NamedValue> l, @Nonnull String newName) {
    assertEquals(1, l.size());
    return BookModel.NamedValue.newBuilder(l.get(0)).setName(newName).build();
  }

  @Nonnull static BookModel.BookPageIds toPageIds(@Nonnull BookModel.BookPageData page) {
    final BookModel.BookPageIds.Builder builder = BookModel.BookPageIds.newBuilder();

    builder.addAllGenreIds(getIds(page.getGenresList()));
    builder.addAllLanguageIds(getIds(page.getLanguagesList()));
    builder.addAllPersonIds(getIds(page.getPersonsList()));
    builder.addAllOriginIds(getIds(page.getOriginsList()));
    builder.addAllSeriesIds(getIds(page.getSeriesList()));
    builder.addAllExternalBookTypeIds(getIds(page.getExternalBookTypesList()));

    for (final BookModel.BookMeta book : page.getBooksList()) {
      builder.addBookIds(book.getId());
    }

    return builder.build();
  }

  @Nonnull static List<Long> getIds(@Nonnull Iterable<BookModel.NamedValue> values) {
    final List<Long> result = new ArrayList<>();
    for (final BookModel.NamedValue val : values) {
      result.add(val.getId());
    }
    return result;
  }
}
