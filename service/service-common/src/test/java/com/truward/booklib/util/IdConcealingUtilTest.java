package com.truward.booklib.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Alexander Shabanov
 */
public class IdConcealingUtilTest {

  @Test
  public void shouldConcealIntValue() {
    // Given:
    final int value = 123;

    // When:
    final String id = IdConcealingUtil.toString(value);

    // Then:
    assertEquals(value, IdConcealingUtil.parseHexInt(id, "id"));
  }

  @Test
  public void shouldConcealLongValue() {
    // Given:
    final long value = 123L;

    // When:
    final String id = IdConcealingUtil.toString(value);

    // Then:
    assertEquals(value, IdConcealingUtil.parseHexLong(id, "id"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldFailToParseInvalidIntId() {
    IdConcealingUtil.parseHexInt("123x", "id");
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldFailToParseInvalidLongId() {
    IdConcealingUtil.parseHexLong("123x", "id");
  }
}
