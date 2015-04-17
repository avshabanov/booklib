package com.truward.booklib.book.server.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Alexander Shabanov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/spring/ExtidServiceTest-context.xml")
@Transactional
public final class ExtidServiceTest {
  @Test
  public void dummy() {}
}
