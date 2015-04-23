package com.truward.booklib.extid.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * @author Alexander Shabanov
 */
public interface ExtIdRestService {

  @RequestMapping(value = "/extid/types", method = RequestMethod.POST)
  ExtId.GetTypesResponse getTypes();

  @RequestMapping(value = "/extid/types", method = RequestMethod.PUT)
  void saveType(@RequestBody ExtId.Type type);

  @RequestMapping(value = "/extid/page/query", method = RequestMethod.POST)
  @ResponseBody
  ExtId.IdList queryByInternalIds(@RequestBody ExtId.QueryByInternalIds request);

  @RequestMapping(value = "/extid/ids", method = RequestMethod.PUT)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void saveIds(@RequestBody ExtId.SaveIdRequest request);

  @RequestMapping(value = "/extid/ids/ints", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void deleteByIntId(@RequestBody ExtId.DeleteByIntIdRequest request);

  @RequestMapping(value = "/extid/ids/types", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void deleteByType(@RequestBody ExtId.DeleteByTypeRequest request);

  @RequestMapping(value = "/extid/groups", method = RequestMethod.GET)
  @ResponseBody
  ExtId.GroupList getGroups();

  @RequestMapping(value = "/extid/groups/query/by-ids", method = RequestMethod.GET)
  @ResponseBody
  ExtId.GroupList queryGroups(@RequestBody ExtId.GetGroupByIdRequest request);

  @RequestMapping(value = "/extid/groups", method = RequestMethod.PUT)
  @ResponseBody
  ExtId.SaveGroupsResponse saveGroup(@RequestBody ExtId.SaveGroupsRequest request);

  @RequestMapping(value = "/extid/groups", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void deleteGroups(@RequestBody ExtId.DeleteGroupsRequest request);
}
