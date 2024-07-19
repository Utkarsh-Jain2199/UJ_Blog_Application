package com.mountblue.blogapplication.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;


import javax.sql.DataSource;

@Configuration
public class SecurityConfig {

    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource) {
        JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager(dataSource);
        userDetailsManager.setUsersByUsernameQuery("SELECT email, password, true AS enabled FROM users WHERE email = ?");
        userDetailsManager.setAuthoritiesByUsernameQuery("SELECT email, role FROM users WHERE email = ?");
        return userDetailsManager;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                //.csrf(csrf->csrf.disable())
                .authorizeHttpRequests(configure ->
                        configure
                                //  .requestMatchers("/**").hasRole("author")
                               // .requestMatchers(HttpMethod.GET, "po")
                                .requestMatchers("/","/api/**","/css/**","/images/**","/posts/{postId}").permitAll()
                                .requestMatchers("/access-denied", "/register").permitAll()
                                .requestMatchers("/addUser", "/comment/{postId}").permitAll()
                                .anyRequest().authenticated()
                )
                .formLogin(form ->
                        form
                                .loginPage("/loginPage")
                                .loginProcessingUrl("/authenticateTheUser")
                                .defaultSuccessUrl("/")
                                .permitAll()
                )
                .logout(LogoutConfigurer::permitAll)
                .exceptionHandling(configure ->
                        configure.accessDeniedPage("/access-denied")
                );
        //http.httpBasic(Customizer.withDefaults());
        return http.build();
    }

}
