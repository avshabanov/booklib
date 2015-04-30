package com.truward.extid.server.controller;

import com.truward.extid.model.ExtId;
import com.truward.extid.model.IdPairsRestService;
import com.truward.extid.server.service.IdPairsService;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
@Controller
@RequestMapping("/rest/extid/id-pairs")
public final class IdPairsRestController implements IdPairsRestService {
  private final IdPairsService.Contract idPairsService;

  public IdPairsRestController(@Nonnull IdPairsService.Contract idPairsService) {
    Assert.notNull(idPairsService, "idPairsService");
    this.idPairsService = idPairsService;
  }

  @Override
  public ExtId.IdPairList queryIdPairs(@RequestBody ExtId.QueryIdPairs request) {
    return ExtId.IdPairList.newBuilder().addAllIdPairs(idPairsService.queryIdPairs(request)).build();
  }

  @Override
  public void saveIdPairs(@RequestBody ExtId.SaveIdPairsRequest request) {
    idPairsService.saveIdPairs(request.getIdPairsList());
  }

  @Override
  public void deleteIdPairsByIntIds(@RequestBody ExtId.DeleteIdPairsByIntIdsRequest request) {
    idPairsService.deleteIdPairsByIntIds(request.getIntIdsList());
  }

  @Override
  public void deleteIdPairsByTypes(@RequestBody ExtId.DeleteIdPairsByTypesRequest request) {
    idPairsService.deleteIdPairsByTypeIds(request.getTypeIdsList());
  }

  @Override
  public void deleteIdPairsByGroups(@RequestBody ExtId.DeleteIdPairsByGroupsRequest request) {
    idPairsService.deleteIdPairsByGroupIds(request.getGroupIdsList());
  }
}
