package com.truward.booklib.website.controller;

import com.truward.book.model.BookModel;
import com.truward.book.model.BookRestService;
import com.truward.booklib.model.Booklib;
import com.truward.extid.model.GroupsRestService;
import com.truward.extid.model.IdPairsRestService;
import com.truward.extid.model.TypesRestService;
import com.truward.p13n.model.FavoritesRestService;
import org.junit.Test;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Alexander Shabanov
 */
public final class AjaxControllerLogicTest {
  final BookRestService bookRestService = mock(BookRestService.class);
  final FavoritesRestService favoritesRestService = mock(FavoritesRestService.class);
  final TypesRestService typesRestService = mock(TypesRestService.class);
  final GroupsRestService groupsRestService = mock(GroupsRestService.class);
  final IdPairsRestService idPairsRestService = mock(IdPairsRestService.class);

  final AjaxController controller = new AjaxController(bookRestService, favoritesRestService, typesRestService,
      groupsRestService, idPairsRestService);

  @Test
  public void shouldCopyBookPageIds() {
    // Given:
    when(bookRestService.savePage(any(BookModel.BookPageData.class)))
        .thenReturn(BookModel.BookPageIds.newBuilder()
            .addBookIds(1).addOriginIds(2).addGenreIds(3).addLanguageIds(4).addPersonIds(5).addSeriesIds(6)
            .build());

    // When:
    final Booklib.BookPageIds ids = controller.savePage(BookModel.BookPageData.newBuilder().build());

    // Then:
    assertNotNull(ids);
    assertEquals(singletonList(1L), ids.getBookIdsList());
    assertEquals(singletonList(2L), ids.getOriginIdsList());
    assertEquals(singletonList(3L), ids.getGenreIdsList());
    assertEquals(singletonList(4L), ids.getLanguageIdsList());
    assertEquals(singletonList(5L), ids.getPersonIdsList());
    assertEquals(singletonList(6L), ids.getSeriesIdsList());
  }
}
