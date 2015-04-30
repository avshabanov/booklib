package com.truward.extid.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * @author Alexander Shabanov
 */
public interface IdPairsRestService {
  @RequestMapping(value = "/query", method = RequestMethod.POST)
  @ResponseBody
  ExtId.IdPairList queryIdPairs(@RequestBody ExtId.QueryIdPairs request);

  @RequestMapping(method = RequestMethod.PUT)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void saveIdPairs(@RequestBody ExtId.SaveIdPairsRequest request);

  @RequestMapping(value = "/int-ids", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void deleteIdPairsByIntIds(@RequestBody ExtId.DeleteIdPairsByIntIdsRequest request);

  @RequestMapping(value = "/types", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void deleteIdPairsByTypes(@RequestBody ExtId.DeleteIdPairsByTypesRequest request);

  @RequestMapping(value = "/groups", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void deleteIdPairsByGroups(@RequestBody ExtId.DeleteIdPairsByGroupsRequest request);
}
