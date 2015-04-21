package com.truward.booklib.extid.server.controller;

import com.truward.booklib.extid.model.ExtId;
import com.truward.booklib.extid.model.ExtidRestService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * @author Alexander Shabanov
 */
@Controller
@RequestMapping("/rest/extid")
public final class ExtidRestController implements ExtidRestService {

  @Override
  public ExtId.GetTypesResponse getTypes() {
    return ExtId.GetTypesResponse.newBuilder().build();
  }

  @Override
  public ExtId.IdPage queryByInternalIds(@RequestBody ExtId.QueryByInternalIds request) {
    return ExtId.IdPage.newBuilder().build();
  }

  @Override
  public void saveIds(@RequestBody ExtId.SaveIdRequest request) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void deleteByIntId(@RequestBody ExtId.DeleteByIntIdRequest request) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void deleteByType(@RequestBody ExtId.DeleteByTypeRequest request) {
    throw new UnsupportedOperationException();
  }
}
