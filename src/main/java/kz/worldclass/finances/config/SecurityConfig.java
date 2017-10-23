package kz.worldclass.finances.config;

import kz.worldclass.finances.auth.AuthenticationUpdateFilter;
import kz.worldclass.finances.auth.CustomAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private CustomAuthenticationProvider customAuthenticationProvider;
    @Autowired
    private PersistentTokenRepository persistentTokenRepository;
    @Autowired
    private RememberMeServices rememberMeServices;
    @Autowired
    private AuthenticationUpdateFilter authenticationUpdateFilter;

    @Autowired
    public void registerGlobalAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(customAuthenticationProvider);
    }

    @Bean(name="authenticationManager")
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf()
                    .disable()
                .authorizeRequests()
                    .antMatchers("/", "/js/**", "/images/**", "/components/**").permitAll()
                    .antMatchers("/auth/info", "/auth/login", "/auth/logout").permitAll()
                    .antMatchers("/auth-manage/**").hasRole("ADMIN")
                    .antMatchers("/dict/**").hasRole("ADMIN")
                    .antMatchers("/cash-report/**").hasAnyRole("HQ", "FILIAL_USER")
                        .antMatchers("/cash-report/affiliate/**").hasRole("FILIAL_USER")
                        .antMatchers("/cash-report/hq/**").hasRole("HQ")
                    .anyRequest().authenticated()
                    .and()
//                .rememberMe()
//                    .rememberMeServices(rememberMeServices)
//                    .tokenRepository(persistentTokenRepository)
//                    .tokenValiditySeconds(1209600)
//                    .and()
                .addFilterAfter(authenticationUpdateFilter, LogoutFilter.class);
    }

	@Bean
	public SavedRequestAwareAuthenticationSuccessHandler savedRequestAwareAuthenticationSuccessHandler() {
        SavedRequestAwareAuthenticationSuccessHandler auth = new SavedRequestAwareAuthenticationSuccessHandler();
		auth.setTargetUrlParameter("targetUrl");
		return auth;
	}
}