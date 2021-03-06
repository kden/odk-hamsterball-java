package org.benetech.configuration;

import org.benetech.security.WebServiceDelegatingAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@Profile({"default", "integrationtest", "unittest"})
@ComponentScan(basePackages = {"org.benetech"})
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Autowired
  private WebClientConfiguration webClientConfiguration;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().ignoringAntMatchers("/file/**");

    http.authorizeRequests().antMatchers("/css/**").permitAll().antMatchers("/healthcheck")
        .permitAll().antMatchers("/images/**").permitAll().antMatchers("/favicon.ico").permitAll()
        .anyRequest().authenticated().and().formLogin().loginPage("/login")
        .failureUrl("/login?error").permitAll().and().logout().permitAll()
        // .exceptionHandling() can hide information about errors, uncomment when debugging for more information
        .and().exceptionHandling()
        .accessDeniedPage("/login?error");

  }

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth.authenticationProvider(authenticationProvider());
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    WebServiceDelegatingAuthenticationProvider authenticationProvider =
        new WebServiceDelegatingAuthenticationProvider();
    authenticationProvider.setWebServicesProperties(webClientConfiguration.webServicesProperties());
    return authenticationProvider;
  }
}
