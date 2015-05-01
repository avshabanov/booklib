package com.truward.p13n.server.controller;

import com.truward.p13n.model.P13n;
import com.truward.p13n.server.service.FavoritesService;
import org.junit.Test;

import java.util.*;

import static com.truward.p13n.server.DomainTestUtil.extId;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Alexander Shabanov
 */
public final class FavoritesRestControllerTest {
  final FavoritesService.Contract favoritesService = mock(FavoritesService.Contract.class);
  final FavoritesRestController controller = new FavoritesRestController(favoritesService);

  final long userId = 10000L;
  final int type1 = 10;
  final int type2 = 20;
  final List<Integer> types = asList(type1, type2);

  @Test
  public void shouldConvertExtIdsToMap() {
    // Given:
    when(favoritesService.getAllFavorites(userId, types))
        .thenReturn(asList(extId(1L, type1), extId(2L, type1), extId(1L, type2)));

    // When:
    final P13n.GetAllFavoritesResponse response = controller.getAllFavorites(P13n.GetAllFavoritesRequest.newBuilder()
        .setUserId(userId).addTypes(type1).addTypes(type2).build());

    // Then:
    assertEquals(2, response.getEntriesCount());

    // Convert entries to map and check structure
    final Map<Integer, Set<Long>> typeToIdsMap = new HashMap<>();
    for (final P13n.GetAllFavoritesResponse.Entry e : response.getEntriesList()) {
      Set<Long> ids = typeToIdsMap.get(e.getType());
      assertNull(ids); // should be only one
      ids = new HashSet<>();
      for (final Long id : e.getIdsList()) {
        assertTrue(ids.add(id));
      }
      typeToIdsMap.put(e.getType(), ids);
    }

    final Map<Integer, Set<Long>> expectedMap = new HashMap<>();
    expectedMap.put(type1, new HashSet<>(asList(1L, 2L)));
    expectedMap.put(type2, new HashSet<>(singletonList(1L)));
    assertEquals(expectedMap, typeToIdsMap);
  }

  @Test
  public void shouldReturnEmptyList() {
    // Given:
    when(favoritesService.getAllFavorites(userId, types))
        .thenReturn(Collections.<P13n.ExtId>emptyList());

    // When:
    final P13n.GetAllFavoritesResponse response = controller.getAllFavorites(P13n.GetAllFavoritesRequest.newBuilder()
        .setUserId(userId).addTypes(type1).addTypes(type2).build());

    // Then:
    assertEquals(0, response.getEntriesCount());
  }
}
