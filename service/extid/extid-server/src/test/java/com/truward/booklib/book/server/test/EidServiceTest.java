package com.truward.booklib.book.server.test;

import com.truward.booklib.extid.model.ExtId;
import com.truward.booklib.extid.server.service.EidService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Alexander Shabanov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/spring/EidServiceTest-context.xml")
@Transactional
public final class EidServiceTest {
  @Resource EidService.Contract extidService;

  @Test
  public void shouldQueryAllIds() {
    // Given:
    final ExtId.QueryIdPairs request = ExtId.QueryIdPairs.newBuilder()
        .addIntIds(ExtId.IntId.newBuilder().setId(3).setTypeId(10).build()).setIncludeAllGroupIds(true)
        .build();

    // When:
    final List<ExtId.IdPair> list = extidService.queryIdPairs(request);

    // Then:
    assertFalse(list.isEmpty());
  }

  @Test
  public void shouldReturnNothingForNoIdsAndTypeIds() {
    // Given:
    final ExtId.QueryIdPairs request = ExtId.QueryIdPairs.newBuilder().setIncludeAllGroupIds(true).build();

    // When:
    final List<ExtId.IdPair> list = extidService.queryIdPairs(request);

    // Then:
    assertTrue(list.isEmpty());
  }

  @Test
  public void shouldGetType() {
    // When:
    final List<ExtId.Type> types = extidService.getTypes();

    // Then:
    assertEquals(Collections.singletonList(ExtId.Type.newBuilder().setId(10).setName("BOOK").build()), types);
  }

  @Test
  public void shouldPersistTypes() {
    // Given:
    final ExtId.Type type1 = ExtId.Type.newBuilder().setId(10).setName("PAPER_BOOK").build();
    final ExtId.Type type2 = ExtId.Type.newBuilder().setName("MOVIE").build();

    // When:
    extidService.saveType(type1);
    extidService.saveType(type2);

    // Then:
    assertEquals(Arrays.asList(type1, ExtId.Type.newBuilder(type2).setId(50).build()), extidService.getTypes());
  }

  @Test
  public void shouldSaveGroups() {
    // Given:
    final ExtId.Group group1 = ExtId.Group.newBuilder().setId(20).setCode("alibaba").build();
    final ExtId.Group group2 = ExtId.Group.newBuilder().setCode("7wonders").build();

    // When:
    final List<Integer> ids = extidService.saveGroups(Arrays.asList(group1, group2));

    // Then:
    assertEquals(Arrays.asList(20, 1500), ids);
  }

  @Test
  public void shouldDeleteGroup() {
    // When:
    extidService.deleteIdPairsByIntIds(Collections.singletonList(ExtId.IntId.newBuilder().setId(3L).setTypeId(10).build()));
    extidService.deleteGroups(Collections.singletonList(20));

    // Then:
    final List<ExtId.Group> groups = extidService.getGroups();
    assertEquals(3, groups.size());
  }
}
