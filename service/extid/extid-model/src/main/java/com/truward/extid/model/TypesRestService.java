package com.truward.extid.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * @author Alexander Shabanov
 */
public interface TypesRestService {

  @RequestMapping(method = RequestMethod.GET)
  @ResponseBody
  ExtId.GetTypesResponse getTypes();

  @RequestMapping(method = RequestMethod.PUT)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void saveType(@RequestBody ExtId.Type type);
}
