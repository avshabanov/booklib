package com.truward.p13n.server.controller;

import com.truward.p13n.model.FavoritesRestService;
import com.truward.p13n.model.P13n;
import com.truward.p13n.server.service.FavoritesService;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Alexander Shabanov
 */
@Controller
@RequestMapping("/rest/p13n/favorites")
public final class FavoritesRestController implements FavoritesRestService {
  private final FavoritesService.Contract favoritesService;

  public FavoritesRestController(@Nonnull FavoritesService.Contract favoritesService) {
    Assert.notNull(favoritesService, "favoritesService");
    this.favoritesService = favoritesService;
  }

  @Override
  public P13n.GetAllFavoritesResponse getAllFavorites(@RequestBody P13n.GetAllFavoritesRequest request) {
    final List<P13n.ExtId> ids = favoritesService.getAllFavorites(request.getUserId(), request.getTypesList());

    // prepare a map
    final Map<Integer, List<Long>> typeToIdMap = new HashMap<>();
    for (final P13n.ExtId i : ids) {
      List<Long> extIds = typeToIdMap.get(i.getType());
      if (extIds == null) {
        extIds = new ArrayList<>();
        typeToIdMap.put(i.getType(), extIds);
      }

      extIds.add(i.getId());
    }

    // convert map to the response
    final P13n.GetAllFavoritesResponse.Builder builder = P13n.GetAllFavoritesResponse.newBuilder();
    for (final Map.Entry<Integer, List<Long>> e : typeToIdMap.entrySet()) {
      builder.addEntries(P13n.GetAllFavoritesResponse.Entry.newBuilder()
          .setType(e.getKey())
          .addAllIds(e.getValue())
          .build());
    }

    return builder.build();
  }

  @Override
  public P13n.GetFavoritesResponse getFavorites(@RequestBody P13n.GetFavoritesRequest request) {
    return P13n.GetFavoritesResponse.newBuilder()
        .addAllExtIds(favoritesService.getFavorites(request.getUserId(), request.getExtIdsList())).build();
  }

  @Override
  public void setFavorites(@RequestBody P13n.SetFavoritesRequest request) {
    favoritesService.setFavorites(request.getUserId(), request.getEntriesList());
  }
}
