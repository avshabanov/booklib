package com.truward.p13n.server.service;

import com.truward.p13n.model.P13n;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

import static com.truward.p13n.server.DomainTestUtil.extId;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Alexander Shabanov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/spring/ServiceTest-context.xml")
@Transactional
public final class FavoritesServiceTest {
  @Resource FavoritesService.Contract favoritesService;

  final int bookType = 1;
  final int authorType = 2;
  final List<Integer> allTypes = asList(bookType, authorType);

  final long user1 = 301L;
  final long user2 = 302L;
  final long user3 = 303L;

  @Test
  public void shouldSetAndQueryFavorites() {
    // Given:
    final long id1 = 1000L;
    final long id2 = 1002L;
    final long id3 = 1003L;

    // When:
    favoritesService.setFavorites(user1, asList(newSetFavEntry(id1, bookType, true),
        newSetFavEntry(id1, authorType, true), newSetFavEntry(id3, authorType, false)));
    favoritesService.setFavorites(user2, asList(newSetFavEntry(id1, authorType, true),
        newSetFavEntry(id2, authorType, true)));

    // Then:
    assertTrue(favoritesService.getAllFavorites(user1, Collections.<Integer>emptyList()).isEmpty());
    assertTrue(favoritesService.getAllFavorites(user3, allTypes).isEmpty());

    assertEquals(asList(extId(id1, bookType), extId(id1, authorType)),
        favoritesService.getAllFavorites(user1, allTypes));
    assertEquals(singletonList(extId(id1, bookType)),
        favoritesService.getAllFavorites(user1, singletonList(bookType)));
    assertEquals(asList(extId(id1, authorType), extId(id2, authorType)),
        favoritesService.getAllFavorites(user2, allTypes));
  }

  @Test
  public void shouldQueryEmptyFavorites() {
    assertTrue(favoritesService.getAllFavorites(user1, allTypes).isEmpty());
    assertTrue(favoritesService.getAllFavorites(user2, allTypes).isEmpty());
    assertTrue(favoritesService.getAllFavorites(user3, allTypes).isEmpty());
  }

  @Test
  public void shouldResetFavorite() {
    // Given:
    final long id1 = 1000L;

    // When:
    favoritesService.setFavorites(user1, singletonList(newSetFavEntry(id1, bookType, true)));
    favoritesService.setFavorites(user1, singletonList(newSetFavEntry(id1, bookType, false)));

    // Then:
    assertTrue(favoritesService.getAllFavorites(user1, allTypes).isEmpty());
  }

  //
  // Private
  //

  private static P13n.SetFavoritesRequest.Entry newSetFavEntry(long id, int type, boolean favorite) {
    return P13n.SetFavoritesRequest.Entry.newBuilder().setExtId(extId(id, type)).setFavorite(favorite).build();
  }
}
