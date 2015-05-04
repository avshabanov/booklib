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

  // TODO: should go to the brikar

  interface PackedParameterInfo {
    @Nonnull String getParsedArgumentName();
    int size();
    int getEstimatedPackedSize(@Nonnull Object[] parameters);
    @Nonnull Class<?> getClassOfParameter(int pos);
  }

  abstract class BasePackedParameterInfo implements PackedParameterInfo {
    @Nonnull
    @Override
    public String getParsedArgumentName() {
      return "offsetToken";
    }

    @Override
    public int getEstimatedPackedSize(@Nonnull Object[] parameters) {
      int i = parameters.length; // separator
      for (final Object p : parameters) {
        if (p instanceof String) {
          i += ((String) p).length();
        } else if (p instanceof Long) {
          i += 21;
        } else if (p instanceof Integer) {
          i += 11;
        } else {
          i += 10; // unknown type
        }
      }

      return i;
    }
  }

  @Nonnull
  public static Object[] unpack(@Nonnull PackedParameterInfo info, @Nonnull String value) {
    final Object[] result = new Object[info.size()];

    int i = 0;

    return result;
  }

  @Nonnull
  public static String pack(@Nonnull PackedParameterInfo info, @Nonnull Object... parameters) {
    if (info.size() != parameters.length) {
      throw new IllegalStateException("Error while packing " + info.getParsedArgumentName() +
          ". Parameter size mismatch. Actual: " + parameters.length +
          ", expected: " + info.size());
    }

    final StringBuilder builder = new StringBuilder(info.getEstimatedPackedSize(parameters));

    for (int i = 0; i < parameters.length; ++i) {
      final Class<?> clazz = info.getClassOfParameter(i);
      final Object parameter = parameters[i];

      if (parameter == null || clazz.equals(parameter.getClass())) {
        throw new IllegalStateException("Error while packing " + info.getParsedArgumentName() +
            ". Parameter #" + i + " is null or is not of type " + clazz);
      }

      if (i > 0) {
        builder.append(';');
      }

      if (clazz.equals(String.class)) {
        builder.append(parameter.toString());
      } else if (clazz.equals(Long.class)) {
        builder.append((long) parameter);
      } else if (clazz.equals(Integer.class)) {
        builder.append((int) parameter);
      } else {
        throw new IllegalArgumentException("Error while packing " + info.getParsedArgumentName() +
            ". Parameter #" + i + " is of unknown type " + clazz);
      }
    }

    return builder.toString();
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
