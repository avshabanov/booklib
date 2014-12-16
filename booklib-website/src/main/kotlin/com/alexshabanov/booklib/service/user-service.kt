package com.alexshabanov.booklib.service

import java.util.Collections
import java.util.ArrayList

import com.truward.ascetic.Request
import com.truward.ascetic.Response
import com.truward.ascetic.CallResult
import com.truward.ascetic.CallResultCreator
import com.truward.ascetic.ImmediateCallResultCreator
import com.truward.ascetic.Context
import com.truward.ascetic.Service
import com.truward.ascetic.DefaultContext
import com.truward.ascetic.ServiceSupport

//
// Service API
//

data class UserProfile(val id: Long?,
                       val name: String,
                       val avatarUrl: String)

data class GetUserProfilesResponse(val profiles: List<UserProfile>): Response

data class GetUserProfilesRequest(override val context: Context = DefaultContext(),
                                  val userIds: List<Long>): Request

/** User service interface. */
trait UserService: Service {
  fun getUserProfiles(request: GetUserProfilesRequest): CallResult<GetUserProfilesResponse>
}

//
// Service Implementation
//

/** User service implementation. */
class UserServiceImpl(override val resultCreator: CallResultCreator): ServiceSupport, UserService {
  override fun getUserProfiles(request: GetUserProfilesRequest): CallResult<GetUserProfilesResponse> {
    return resultFrom(request, {
      val result = ArrayList<UserProfile>(it.userIds.size)
      for (id in it.userIds) {
        result.add(UserProfile(id = id, name = "user#${id}", avatarUrl = "http://avatarUrl_${id}"))
      }
      GetUserProfilesResponse(profiles = result)
    })
  }
}
