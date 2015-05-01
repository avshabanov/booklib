package com.truward.p13n.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * @author Alexander Shabanov
 */
public interface FavoritesRestService {

  @RequestMapping(value = "/query", method = RequestMethod.POST)
  @ResponseBody
  P13n.GetAllFavoritesResponse getAllFavorites(@RequestBody P13n.GetAllFavoritesRequest request);

  @RequestMapping(value = "/items/query", method = RequestMethod.POST)
  @ResponseBody
  P13n.GetFavoritesResponse getFavorites(@RequestBody P13n.GetFavoritesRequest request);

  @RequestMapping(value = "/items", method = RequestMethod.PUT)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void setFavorites(@RequestBody P13n.SetFavoritesRequest request);
}
