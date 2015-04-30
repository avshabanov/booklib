package com.truward.extid.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * @author Alexander Shabanov
 */
public interface GroupsRestService {

  @RequestMapping(method = RequestMethod.GET)
  @ResponseBody
  ExtId.GroupList getGroups();

  @RequestMapping(value = "/query/by-ids", method = RequestMethod.GET)
  @ResponseBody
  ExtId.GroupList queryGroups(@RequestBody ExtId.GetGroupByIdRequest request);

  @RequestMapping(method = RequestMethod.PUT)
  @ResponseBody
  ExtId.SaveGroupsResponse saveGroup(@RequestBody ExtId.SaveGroupsRequest request);

  @RequestMapping(method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void deleteGroups(@RequestBody ExtId.DeleteGroupsRequest request);
}
