package com.truward.booklib.extid.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * @author Alexander Shabanov
 */
public interface ExtidRestService {

  @RequestMapping(value = "/extid/query", method = RequestMethod.POST)
  @ResponseBody
  ExtId.IdPage queryByInternalIds(@RequestBody ExtId.QueryByInternalIds request);

  @RequestMapping(value = "/extid", method = RequestMethod.PUT)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void saveIds(@RequestBody ExtId.SaveIdRequest request);

  @RequestMapping(value = "/extid/int-id", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void deleteByIntId(@RequestBody ExtId.DeleteByIntIdRequest request);

  @RequestMapping(value = "/extid/type", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void deleteByType(@RequestBody ExtId.DeleteByTypeRequest request);
}
