package com.truward.booklib.book.server.test;

import com.truward.booklib.book.model.BookModel;
import com.truward.booklib.book.model.BookRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Alexander Shabanov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/spring/BookServiceTest-context.xml")
public class BookServiceTest {

  @Resource
  private BookRestService bookService;

  @Test
  public void shouldGetAllGenres() {
    // Given:

    // When:
    final BookModel.NamedValueList list = bookService.getGenres();

    // Then:
    assertTrue(list.getValuesCount() > 0);
  }

  @Test
  public void shouldQueryBooks() {
    final BookModel.BookSublist list = bookService.queryBooks(BookModel.BookPageQuery.newBuilder()
        .setLimit(2)
        .setSortType(BookModel.BookPageQuery.SortType.ID)
        .build());
    assertEquals(2, list.getBookIdsCount());
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
}
