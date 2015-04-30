package com.truward.extid.server.controller;

import com.truward.extid.model.ExtId;
import com.truward.extid.model.TypesRestService;
import com.truward.extid.server.service.TypesService;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
@Controller
@RequestMapping("/rest/extid/types")
public final class TypesRestController implements TypesRestService {
  private final TypesService.Contract typesService;

  public TypesRestController(@Nonnull TypesService.Contract typesService) {
    Assert.notNull(typesService, "typesService");
    this.typesService = typesService;
  }

  @Override
  public ExtId.GetTypesResponse getTypes() {
    return ExtId.GetTypesResponse.newBuilder().addAllTypes(typesService.getTypes()).build();
  }

  @Override
  public void saveType(@RequestBody ExtId.Type type) {
    typesService.saveType(type);
  }
}
