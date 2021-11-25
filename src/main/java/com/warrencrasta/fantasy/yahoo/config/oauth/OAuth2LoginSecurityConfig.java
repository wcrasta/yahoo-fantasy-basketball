package com.warrencrasta.fantasy.yahoo.config.oauth;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.firewall.HttpStatusRequestRejectedHandler;
import org.springframework.security.web.firewall.RequestRejectedHandler;

@EnableWebSecurity
public class OAuth2LoginSecurityConfig extends WebSecurityConfigurerAdapter {

  /* https://www.baeldung.com/spring-security-5-oauth2-login */
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
        .antMatchers("/signin", "/examples", "/contact", "/css/**", "/js/**", "/images/**")
        .permitAll()
        .anyRequest()
        .authenticated()
        .and()
        .oauth2Login()
        .defaultSuccessUrl("/weekly-matchups", true)
        .loginPage("/signin");
  }

  /*
  * Changes RequestRejectedException logs level to DEBUG.
  * https://stackoverflow.com/a/48644226/7124380
  */
  @Bean
  RequestRejectedHandler requestRejectedHandler() {
    return new HttpStatusRequestRejectedHandler();
  }
}
