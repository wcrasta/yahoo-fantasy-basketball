package com.warrencrasta.fantasy.yahoo.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UnauthenticatedController {
  @GetMapping("/")
  public String redirectToSignIn() {
    return "redirect:/signin";
  }

  @GetMapping("/signin")
  public String getSignInPage() {
    return "signin";
  }

  @GetMapping("/examples")
  public String getExamplesPage() {
    return "examples";
  }

  @GetMapping("/contact")
  public String getContactPage() {
    return "contact";
  }
}
