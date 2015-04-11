package com.truward.booklib.book.server.test;

import com.truward.booklib.book.model.BookModel;
import com.truward.booklib.book.model.BookRestService;
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

    final BookModel.BookPageData page = bookService.getPage(BookModel.BookPageIds.newBuilder()
        .addAllBookIds(list.getBookIdsList())
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
  }

  @Test
  public void shouldQueryPersons() {
    // Given:
    final BookModel.PersonListRequest request = BookModel.PersonListRequest.newBuilder()
        .setLimit(64)
        .build();

    // When:
    final BookModel.NamedValueList persons = bookService.queryPersons(request);

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

    // When:
    final BookModel.BookPageIds ids = bookService.savePage(BookModel.BookPageData.newBuilder()
        .addGenres(BookModel.NamedValue.newBuilder().setName(genre)).build());

    // Then:
    final BookModel.BookPageData pageData = bookService.getPage(ids);
    assertEquals(1, pageData.getGenresCount());
    assertEquals(genre, pageData.getGenres(0).getName());
  }

  //
  // Private
  //

  @Nonnull static BookModel.BookPageIds toPageIds(@Nonnull BookModel.BookPageData page) {
    final BookModel.BookPageIds.Builder builder = BookModel.BookPageIds.newBuilder().setFetchBookDependencies(false);

    builder.addAllGenreIds(getIds(page.getGenresList()));
    builder.addAllLanguageIds(getIds(page.getLanguagesList()));
    builder.addAllPersonIds(getIds(page.getPersonsList()));
    builder.addAllOriginIds(getIds(page.getOriginsList()));

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
