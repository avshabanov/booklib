package com.truward.extid.server.test;

import com.truward.extid.model.ExtId;
import com.truward.extid.server.service.TypesService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Alexander Shabanov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/spring/ServiceTest-context.xml")
@Transactional
public final class TypesServiceTest {
  @Resource TypesService.Contract typesService;

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
        ExtId.Type.newBuilder(type2).setId(1000000).build()), typesService.getTypes());
  }
}
