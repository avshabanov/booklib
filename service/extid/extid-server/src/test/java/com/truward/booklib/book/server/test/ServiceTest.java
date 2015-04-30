package com.truward.booklib.book.server.test;

import com.truward.extid.model.ExtId;
import com.truward.extid.server.service.GroupsService;
import com.truward.extid.server.service.IdPairsService;
import com.truward.extid.server.service.TypesService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Alexander Shabanov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/spring/ServiceTest-context.xml")
@Transactional
public final class ServiceTest {
  @Resource TypesService.Contract typesService;
  @Resource GroupsService.Contract groupsService;
  @Resource IdPairsService.Contract idPairsService;

  @Test
  public void shouldQueryAllIds() {
    // Given:
    final ExtId.QueryIdPairs request = ExtId.QueryIdPairs.newBuilder()
        .addIntIds(ExtId.IntId.newBuilder().setId(3).setTypeId(10).build()).setIncludeAllGroupIds(true)
        .build();

    // When:
    final List<ExtId.IdPair> list = idPairsService.queryIdPairs(request);

    // Then:
    assertFalse(list.isEmpty());
  }

  @Test
  public void shouldReturnNothingForNoIdsAndTypeIds() {
    // Given:
    final ExtId.QueryIdPairs request = ExtId.QueryIdPairs.newBuilder().setIncludeAllGroupIds(true).build();

    // When:
    final List<ExtId.IdPair> list = idPairsService.queryIdPairs(request);

    // Then:
    assertTrue(list.isEmpty());
  }

  @Test
  public void shouldGetType() {
    // When:
    final List<ExtId.Type> types = typesService.getTypes();

    // Then:
    assertEquals(Arrays.asList(ExtId.Type.newBuilder().setId(10).setName("BOOK").build(),
        ExtId.Type.newBuilder().setId(11).setName("PERSON").build()), types);
  }

  @Test
  public void shouldPersistTypes() {
    // Given:
    final ExtId.Type type1 = ExtId.Type.newBuilder().setId(10).setName("PAPER_BOOK").build();
    final ExtId.Type type2 = ExtId.Type.newBuilder().setName("MOVIE").build();

    // When:
    typesService.saveType(type1);
    typesService.saveType(type2);

    // Then:
    assertEquals(Arrays.asList(type1, ExtId.Type.newBuilder().setId(11).setName("PERSON").build(),
        ExtId.Type.newBuilder(type2).setId(50).build()), typesService.getTypes());
  }

  @Test
  public void shouldSaveGroups() {
    // Given:
    final ExtId.Group group1 = ExtId.Group.newBuilder().setId(20).setCode("alibaba").build();
    final ExtId.Group group2 = ExtId.Group.newBuilder().setCode("7wonders").build();

    // When:
    final List<Integer> ids = groupsService.saveGroups(Arrays.asList(group1, group2));

    // Then:
    assertEquals(Arrays.asList(20, 1500), ids);
  }

  @Test
  public void shouldDeleteGroup() {
    // When:
    idPairsService.deleteIdPairsByIntIds(Collections.singletonList(ExtId.IntId.newBuilder().setId(3L).setTypeId(10).build()));
    groupsService.deleteGroups(Collections.singletonList(20));

    // Then:
    final List<ExtId.Group> groups = groupsService.getGroups();
    assertEquals(3, groups.size());
  }
}
