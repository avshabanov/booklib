package com.truward.booklib.book.server.test;

import com.truward.booklib.extid.model.ExtId;
import com.truward.booklib.extid.model.ExtidRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static org.junit.Assert.assertTrue;

/**
 * @author Alexander Shabanov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/spring/ExtidServiceTest-context.xml")
@Transactional
public final class ExtidServiceTest {
  @Resource ExtidRestService extidService;

  @Test
  public void shouldQueryAllIds() {
    // Given:
    final ExtId.QueryByInternalIds request = ExtId.QueryByInternalIds.newBuilder()
        .addTypeIds(10).addIntIds(3).setIncludeAllGroupIds(true)
        .build();

    // When:
    final ExtId.IdPage page = extidService.queryByInternalIds(request);

    // Then:
    assertTrue(page.getIdsCount() > 0);
    assertTrue(page.getGroupsCount() > 0);
  }

  @Test
  public void shouldReturnNothingForNoIdsAndTypeIds() {
    // Given:
    final ExtId.QueryByInternalIds request = ExtId.QueryByInternalIds.newBuilder().setIncludeAllGroupIds(true).build();

    // When:
    final ExtId.IdPage page = extidService.queryByInternalIds(request);

    // Then:
    assertTrue(page.getIdsCount() == 0);
    assertTrue(page.getGroupsCount() == 0);
  }
}
