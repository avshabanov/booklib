package com.truward.book.server.util;

import com.truward.book.model.BookModel;

import javax.annotation.Nonnull;

/**
 * Mapper utility between SQL and protobuf model for PersonRelation.Type
 *
 * @author Alexander Shabanov
 */
public final class PersonRoleMapper {
  private PersonRoleMapper() {}

  public static int toCode(@Nonnull BookModel.PersonRelation.Type type) {
    switch (type) {
      case AUTHOR:
        return 1;
      case ILLUSTRATOR:
        return 2;
      default:
        return 1000;
    }
  }

  @Nonnull
  public static BookModel.PersonRelation.Type fromCode(int code) {
    switch (code) {
      case 1:
        return BookModel.PersonRelation.Type.AUTHOR;
      case 2:
        return BookModel.PersonRelation.Type.ILLUSTRATOR;
      default:
        return BookModel.PersonRelation.Type.UNKNOWN;
    }
  }
}
