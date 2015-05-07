package com.truward.extid.server.test;

import com.truward.extid.model.ExtId;
import com.truward.extid.server.service.GroupsService;
import com.truward.extid.server.service.IdPairsService;
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

/**
 * @author Alexander Shabanov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/spring/ServiceTest-context.xml")
@Transactional
public final class GroupsServiceTest {
  @Resource GroupsService.Contract groupsService;
  @Resource IdPairsService.Contract idPairsService;

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
