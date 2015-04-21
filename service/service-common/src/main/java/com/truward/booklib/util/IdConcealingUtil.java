package com.truward.booklib.util;

import javax.annotation.Nonnull;

/**
 * Utility class for mangling/unmangling IDs. Expected to introduce some level of fool proof rather than
 * cryptographically strong transformations.
 *
 * @author Alexander Shabanov
 */
public final class IdConcealingUtil {
  private IdConcealingUtil() {
  }

  public static long parseHexLong(@Nonnull String value, @Nonnull String parameterName) {
    try {
      return Long.parseLong(value, 16);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Invalid value of " + parameterName, e);
    }
  }

  public static int parseHexInt(@Nonnull String value, @Nonnull String parameterName) {
    try {
      return Integer.parseInt(value, 16);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Invalid value of " + parameterName, e);
    }
  }

  @Nonnull
  public static String toString(long value) {
    return Long.toString(value, 16);
  }

  @Nonnull
  public static String toString(int value) {
    return Integer.toString(value, 16);
  }
}
