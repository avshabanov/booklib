package com.truward.extid.server.controller;

import com.truward.extid.model.ExtId;
import com.truward.extid.model.GroupsRestService;
import com.truward.extid.server.service.GroupsService;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
@Controller
@RequestMapping("/rest/extid/groups")
public final class GroupsRestController implements GroupsRestService {
  private final GroupsService.Contract groupsService;

  public GroupsRestController(@Nonnull GroupsService.Contract groupsService) {
    Assert.notNull(groupsService, "groupsService");
    this.groupsService = groupsService;
  }

  @Override
  public ExtId.GroupList getGroups() {
    return ExtId.GroupList.newBuilder().addAllGroups(groupsService.getGroups()).build();
  }

  @Override
  public ExtId.GroupList queryGroups(@RequestBody ExtId.GetGroupByIdRequest request) {
    return ExtId.GroupList.newBuilder().addAllGroups(groupsService.queryGroups(request.getGroupIdsList())).build();
  }

  @Override
  public ExtId.SaveGroupsResponse saveGroup(@RequestBody ExtId.SaveGroupsRequest request) {
    return ExtId.SaveGroupsResponse.newBuilder().addAllGroupIds(groupsService.saveGroups(request.getGroupsList())).build();
  }

  @Override
  public void deleteGroups(@RequestBody ExtId.DeleteGroupsRequest request) {
    groupsService.deleteGroups(request.getGroupIdsList());
  }
}
