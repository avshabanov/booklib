package com.truward.booklib.book.server.test;

import com.truward.booklib.book.model.BookModel;
import com.truward.booklib.book.model.BookRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

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
    final BookModel.GenreList list = bookService.getGenres();

    // Then:
    assertTrue(list.getGenresCount() > 0);
  }
}
