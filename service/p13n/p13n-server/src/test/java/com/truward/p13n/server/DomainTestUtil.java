package com.truward.p13n.server;

import com.truward.p13n.model.P13n;

/**
 * @author Alexander Shabanov
 */
public final class DomainTestUtil {
  private DomainTestUtil() {}

  public static P13n.ExtId extId(long id, int type) {
    return P13n.ExtId.newBuilder().setId(id).setType(type).build();
  }
}
