package com.truward.booklib.extid.server.controller;

import com.truward.booklib.extid.model.ExtId;
import com.truward.booklib.extid.model.EidRestService;
import com.truward.booklib.extid.server.service.EidService;
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
public final class EidRestController implements EidRestService {
  private final EidService.Contract eidService;

  public EidRestController(@Nonnull EidService.Contract eidService) {
    Assert.notNull(eidService, "extidService");
    this.eidService = eidService;
  }

  @Override
  public ExtId.GetTypesResponse getTypes() {
    return ExtId.GetTypesResponse.newBuilder().addAllTypes(eidService.getTypes()).build();
  }

  @Override
  public void saveType(@RequestBody ExtId.Type type) {
    eidService.saveType(type);
  }

  @Override
  public ExtId.IdPairList queryIdPairs(@RequestBody ExtId.QueryIdPairs request) {
    return ExtId.IdPairList.newBuilder().addAllIdPairs(eidService.queryIdPairs(request)).build();
  }

  @Override
  public void saveIdPairs(@RequestBody ExtId.SaveIdPairsRequest request) {
    eidService.saveIdPairs(request.getIdPairsList());
  }

  @Override
  public void deleteIdPairsByIntIds(@RequestBody ExtId.DeleteIdPairsByIntIdsRequest request) {
    eidService.deleteIdPairsByIntIds(request.getIntIdsList());
  }

  @Override
  public void deleteIdPairsByTypes(@RequestBody ExtId.DeleteIdPairsByTypesRequest request) {
    eidService.deleteIdPairsByTypeIds(request.getTypeIdsList());
  }

  @Override
  public void deleteIdPairsByGroups(@RequestBody ExtId.DeleteIdPairsByGroupsRequest request) {
    eidService.deleteIdPairsByGroupIds(request.getGroupIdsList());
  }

  @Override
  public ExtId.GroupList getGroups() {
    return ExtId.GroupList.newBuilder().addAllGroups(eidService.getGroups()).build();
  }

  @Override
  public ExtId.GroupList queryGroups(@RequestBody ExtId.GetGroupByIdRequest request) {
    return ExtId.GroupList.newBuilder().addAllGroups(eidService.queryGroups(request.getGroupIdsList())).build();
  }

  @Override
  public ExtId.SaveGroupsResponse saveGroup(@RequestBody ExtId.SaveGroupsRequest request) {
    return ExtId.SaveGroupsResponse.newBuilder().addAllGroupIds(eidService.saveGroups(request.getGroupsList())).build();
  }

  @Override
  public void deleteGroups(@RequestBody ExtId.DeleteGroupsRequest request) {
    eidService.deleteGroups(request.getGroupIdsList());
  }
}
