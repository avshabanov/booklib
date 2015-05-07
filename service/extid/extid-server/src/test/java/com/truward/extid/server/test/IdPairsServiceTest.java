package com.truward.extid.server.test;

import com.truward.extid.model.ExtId;
import com.truward.extid.server.service.IdPairsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Alexander Shabanov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/spring/ServiceTest-context.xml")
@Transactional
public final class IdPairsServiceTest {
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
}
