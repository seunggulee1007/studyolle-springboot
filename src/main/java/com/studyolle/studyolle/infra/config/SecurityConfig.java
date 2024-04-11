package com.studyolle.studyolle.infra.config;

import com.google.common.collect.ImmutableList;
import com.studyolle.studyolle.security.EntryPointUnauthorizedHandler;
import com.studyolle.studyolle.security.Jwt;
import com.studyolle.studyolle.security.JwtAccessDeniedHandler;
import com.studyolle.studyolle.security.JwtAuthenticationTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final Jwt jwt;

    private final JwtTokenConfigure jwtTokenConfigure;

    private final JwtAccessDeniedHandler accessDeniedHandler;

    private final EntryPointUnauthorizedHandler unauthorizedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter() {
        return new JwtAuthenticationTokenFilter(jwtTokenConfigure.getHeader(), jwt);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure ( HttpSecurity http ) throws Exception {
        http
            .httpBasic()
                .disable()                                              // rest api 이므로 기본설정 사용안함. 기본설정은 비인증시 로그인폼 화면으로 리다이렉트 된다.
            .csrf()
                .disable()                                              // rest api이므로 csrf 보안이 필요없으므로 disable처리.
            .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint(unauthorizedHandler)
            .and()
                .sessionManagement()
                .sessionCreationPolicy( SessionCreationPolicy.STATELESS) // jwt token으로 인증하므로 세션은 필요없으므로 생성안함.
            .and()
                .cors()
                // .configurationSource(request -> new CorsConfiguration(setCorsConfig()).applyPermitDefaultValues())
                .configurationSource(corsConfigurationSource())
            .and()
                .authorizeRequests()
                .antMatchers( "/*", "/api/login", "/api/signUp", "/api/check-email", "/api/check-email-token",
                        "/api/email-login", "/api/check-email-login", "/api/login-link").permitAll()
                .antMatchers( HttpMethod.GET, "/api/profile/*" ).permitAll()
                .antMatchers("/api/**")
                    .hasRole("USER")
            .and()
                .addFilterBefore(jwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        ;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(ImmutableList.of( "http://localhost:3000", "null"));
        config.setAllowedMethods(ImmutableList.of("GET", "PUT", "DELETE", "POST", "OPTIONS", "HEAD"));
        config.setExposedHeaders(ImmutableList.of("Access-Control-Allow-Headers", "ACCESS_TOKEN", "Access-Control-Allow-Origin", "strict-origin-when-cross-origin"));
        config.setAllowedHeaders(ImmutableList.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Override // ignore check swagger resource
    public void configure( WebSecurity web) {
        web.ignoring().antMatchers("/v2/api-docs", "/swagger-resources/**",
                "/swagger-ui.html", "/webjars/**", "/swagger/**");
    }

}
