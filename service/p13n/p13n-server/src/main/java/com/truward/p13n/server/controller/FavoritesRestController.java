package com.truward.p13n.server.controller;

import com.truward.p13n.model.FavoritesRestService;
import com.truward.p13n.model.P13n;
import com.truward.p13n.server.service.FavoritesService;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Nonnull;

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
    throw new UnsupportedOperationException();
  }

  @Override
  public P13n.GetFavoritesResponse getFavorites(@RequestBody P13n.GetFavoritesResponse request) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setFavorites(@RequestBody P13n.SetFavoritesRequest request) {
    favoritesService.setFavorites(request.getEntriesList());
  }
}
