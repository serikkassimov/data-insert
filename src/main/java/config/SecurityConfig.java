package config;


import auth.CustomAuthenticationProvider;
import auth.CustomUserService;
import auth.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.RememberMeConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
@ComponentScan(basePackages = {"auth"})
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    private CustomAuthenticationProvider customAuthenticationProvider;

    @Autowired
    private CustomUserService customUserService;

    @Autowired
    private TokenRepository tokenRepository;


    @Autowired
    public void registerGlobalAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        // auth.userDetailsService(userDetailsService);
        auth.authenticationProvider(customAuthenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
                .authorizeRequests()
               // .antMatchers("/risk_page_html/**").permitAll()

                //.antMatchers("/resources/**", "/**").permitAll()
                .anyRequest().permitAll()
                .and();


        http.formLogin()
                .loginPage("/pages/login.jsp")
                .loginProcessingUrl("/j_spring_security_check")
                .failureUrl("/pages/login.jsp?error")
                .usernameParameter("j_username")
                .passwordParameter("j_password")
                .permitAll()
                .and().rememberMe()
                .tokenRepository(tokenRepository)
                .userDetailsService(customUserService)
                .tokenValiditySeconds(1209600)
                .key("myAppKey")
        ;

        http.logout()
                .permitAll()
                //.logoutUrl("/pages/logout")
                .logoutSuccessUrl("/pages/login.jsp?logout")
                .invalidateHttpSession(true);
    }

   /* @Bean
    public ShaPasswordEncoder getShaPasswordEncoder(){
        return new ShaPasswordEncoder();
    }
*/
}