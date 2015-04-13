package com.truward.booklib.website.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Alexander Shabanov
 */
@Controller
@RequestMapping("/rest/ajax/")
public class AjaxController {

  @RequestMapping("/books/{id}")
  public void getBookById(HttpServletResponse response) throws IOException {
    response.getWriter().append("{\"book\": 1}");
  }
}
