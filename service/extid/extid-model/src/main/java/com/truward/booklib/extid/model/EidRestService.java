package com.truward.booklib.extid.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * @author Alexander Shabanov
 */
public interface EidRestService {

  @RequestMapping(value = "/types", method = RequestMethod.GET)
  @ResponseBody
  ExtId.GetTypesResponse getTypes();

  @RequestMapping(value = "/types", method = RequestMethod.PUT)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void saveType(@RequestBody ExtId.Type type);

  @RequestMapping(value = "/id-pairs/query", method = RequestMethod.POST)
  @ResponseBody
  ExtId.IdPairList queryIdPairs(@RequestBody ExtId.QueryIdPairs request);

  @RequestMapping(value = "/id-pairs", method = RequestMethod.PUT)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void saveIdPairs(@RequestBody ExtId.SaveIdPairsRequest request);

  @RequestMapping(value = "/id-pairs/int-ids", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void deleteIdPairsByIntIds(@RequestBody ExtId.DeleteIdPairsByIntIdsRequest request);

  @RequestMapping(value = "/id-pairs/types", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void deleteIdPairsByTypes(@RequestBody ExtId.DeleteIdPairsByTypesRequest request);

  @RequestMapping(value = "/id-pairs/groups", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void deleteIdPairsByGroups(@RequestBody ExtId.DeleteIdPairsByGroupsRequest request);

  @RequestMapping(value = "/groups", method = RequestMethod.GET)
  @ResponseBody
  ExtId.GroupList getGroups();

  @RequestMapping(value = "/groups/query/by-ids", method = RequestMethod.GET)
  @ResponseBody
  ExtId.GroupList queryGroups(@RequestBody ExtId.GetGroupByIdRequest request);

  @RequestMapping(value = "/groups", method = RequestMethod.PUT)
  @ResponseBody
  ExtId.SaveGroupsResponse saveGroup(@RequestBody ExtId.SaveGroupsRequest request);

  @RequestMapping(value = "/groups", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void deleteGroups(@RequestBody ExtId.DeleteGroupsRequest request);
}
