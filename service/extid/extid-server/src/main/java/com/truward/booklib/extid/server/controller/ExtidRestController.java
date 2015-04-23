package com.truward.booklib.extid.server.controller;

import com.truward.booklib.extid.model.ExtId;
import com.truward.booklib.extid.model.ExtIdRestService;
import com.truward.booklib.extid.server.service.ExtIdService;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Nonnull;


/**
 * @author Alexander Shabanov
 */
@Controller
@RequestMapping("/rest/extid")
public final class ExtIdRestController implements ExtIdRestService {
  private final ExtIdService.Contract extidService;

  public ExtIdRestController(@Nonnull ExtIdService.Contract extidService) {
    Assert.notNull(extidService, "extidService");
    this.extidService = extidService;
  }

  @Override
  public ExtId.GetTypesResponse getTypes() {
    return ExtId.GetTypesResponse.newBuilder().addAllTypes(extidService.getTypes()).build();
  }

  @Override
  public void saveType(@RequestBody ExtId.Type type) {
    extidService.saveType(type);
  }

  @Override
  public ExtId.IdList queryByInternalIds(@RequestBody ExtId.QueryByInternalIds request) {
    return ExtId.IdList.newBuilder().addAllIds(extidService.queryByInternalIds(request)).build();
  }

  @Override
  public void saveIds(@RequestBody ExtId.SaveIdRequest request) {
    extidService.saveIds(request.getIdsList());
  }

  @Override
  public void deleteByIntId(@RequestBody ExtId.DeleteByIntIdRequest request) {
    extidService.deleteByIntIds(request.getIntIdsList());
  }

  @Override
  public void deleteByType(@RequestBody ExtId.DeleteByTypeRequest request) {
    extidService.deleteByTypeIds(request.getTypeIdsList());
  }

  @Override
  public ExtId.GroupList getGroups() {
    return ExtId.GroupList.newBuilder().addAllGroups(extidService.getGroups()).build();
  }

  @Override
  public ExtId.GroupList queryGroups(@RequestBody ExtId.GetGroupByIdRequest request) {
    return ExtId.GroupList.newBuilder().addAllGroups(extidService.queryGroups(request.getGroupIdsList())).build();
  }

  @Override
  public ExtId.SaveGroupsResponse saveGroup(@RequestBody ExtId.SaveGroupsRequest request) {
    return ExtId.SaveGroupsResponse.newBuilder().addAllGroupIds(extidService.saveGroups(request.getGroupsList())).build();
  }

  @Override
  public void deleteGroups(@RequestBody ExtId.DeleteGroupsRequest request) {
    extidService.deleteGroups(request.getGroupIdsList());
  }
}
