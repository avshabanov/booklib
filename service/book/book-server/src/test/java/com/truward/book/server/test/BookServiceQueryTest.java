package com.truward.book.server.test;

import com.truward.book.model.BookModel;
import com.truward.book.server.service.BookService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests query API in book service.
 *
 * @author Alexander Shabanov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/spring/BookServiceTest-context.xml")
@Transactional
public final class BookServiceQueryTest {
  @Resource BookService.Contract bookService;

  @Test
  public void shouldReturnEmptyList() {
    // Given:
    final BookModel.BookPageQuery query = BookModel.BookPageQuery.newBuilder()
        .setLimit(0)
        .setSortType(BookModel.BookPageQuery.SortType.ID)
        .build();

    // When:
    final BookModel.BookList list = bookService.queryBooks(query);

    // Then:
    assertEquals(0, list.getBookIdsCount());
    assertFalse(list.hasOffsetToken());
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowAnErrorForTooBigLimit() {
    // Given:
    final BookModel.BookPageQuery query = BookModel.BookPageQuery.newBuilder()
        .setLimit(65)
        .setSortType(BookModel.BookPageQuery.SortType.ID)
        .build();

    // When:
    bookService.queryBooks(query);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowAnErrorForNegativeLimit() {
    // Given:
    final BookModel.BookPageQuery query = BookModel.BookPageQuery.newBuilder()
        .setLimit(-1)
        .setSortType(BookModel.BookPageQuery.SortType.ID)
        .build();

    // When:
    bookService.queryBooks(query);
  }

  @Test
  public void shouldQueryBooksOrderedByIdWithEmptyFilter() {
    // page one
    final BookModel.BookPageQuery query1 = BookModel.BookPageQuery.newBuilder()
        .setLimit(2)
        .setSortType(BookModel.BookPageQuery.SortType.ID)
        .build();
    final BookModel.BookList list1 = bookService.queryBooks(query1);
    assertEquals(Arrays.asList(1L, 2L), list1.getBookIdsList());
    assertNotNull(list1.getOffsetToken());

    // next page
    final BookModel.BookList list2 = bookService.queryBooks(BookModel.BookPageQuery.newBuilder(query1)
        .setOffsetToken(list1.getOffsetToken())
        .build());
    assertEquals(Arrays.asList(3L, 4L), list2.getBookIdsList());
  }

  @Test
  public void shouldQueryBooksOrderedByTitleWithEmptyFilter() {
    // Given:
    final BookModel.BookList list = bookService.queryBooks(BookModel.BookPageQuery.newBuilder()
        .setLimit(64).setSortType(BookModel.BookPageQuery.SortType.TITLE)
        .build());

    // When/Then:
    BookModel.BookPageQuery query = BookModel.BookPageQuery.newBuilder()
        .setLimit(2).setSortType(BookModel.BookPageQuery.SortType.TITLE)
        .build();
    int pos = 0;
    int prevCount = 0;

    for (;;) {
      final BookModel.BookList sublist = bookService.queryBooks(query);
      pos += prevCount;
      final int nextPos = Math.min(list.getBookIdsCount(), pos + query.getLimit());
      final List<Long> expectedIds = list.getBookIdsList().subList(pos, nextPos);
      assertEquals(expectedIds, sublist.getBookIdsList());

      if (sublist.getBookIdsCount() < query.getLimit()) {
        break;
      }

      // move to the next page
      prevCount = sublist.getBookIdsCount();
      query = BookModel.BookPageQuery.newBuilder(query).setOffsetToken(sublist.getOffsetToken()).build();
    }
  }

  @Test
  public void shouldQueryAllBooksOrderedByTitle() {
    // Given:
    final BookModel.BookPageQuery query = BookModel.BookPageQuery.newBuilder()
        .setLimit(64)
        .setSortType(BookModel.BookPageQuery.SortType.TITLE)
        .build();

    // When:
    final BookModel.BookList list = bookService.queryBooks(query);

    // Then:
    assertEquals(Arrays.asList(18L, 22L, 16L, 1L, 13L, 8L, 4L, 2L, 20L, 34L, 37L, 30L, 36L, 33L, 19L, 17L,
            6L, 3L, 5L, 11L, 21L, 14L, 31L, 35L, 12L, 32L, 10L, 9L, 15L, 7L),
        list.getBookIdsList());
    assertFalse(list.hasOffsetToken());
  }

  // TODO: language

  // TODO: origin

  // TODO: person

  // TODO: genre
}
