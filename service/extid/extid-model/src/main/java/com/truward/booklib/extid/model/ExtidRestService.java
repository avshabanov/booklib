package com.truward.booklib.extid.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * @author Alexander Shabanov
 */
public interface ExtidRestService {

  @RequestMapping(value = "/extid/types", method = RequestMethod.POST)
  ExtId.GetTypesResponse getTypes();

  @RequestMapping(value = "/extid/page/query", method = RequestMethod.POST)
  @ResponseBody
  ExtId.IdPage queryByInternalIds(@RequestBody ExtId.QueryByInternalIds request);

  @RequestMapping(value = "/extid/ids", method = RequestMethod.PUT)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void saveIds(@RequestBody ExtId.SaveIdRequest request);

  @RequestMapping(value = "/extid/ids/ints", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void deleteByIntId(@RequestBody ExtId.DeleteByIntIdRequest request);

  @RequestMapping(value = "/extid/ids/types", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void deleteByType(@RequestBody ExtId.DeleteByTypeRequest request);
}
