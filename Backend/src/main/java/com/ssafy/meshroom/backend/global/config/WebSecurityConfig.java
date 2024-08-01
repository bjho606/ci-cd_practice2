package com.ssafy.meshroom.backend.global.config;

import com.ssafy.meshroom.backend.domain.user.domain.UserRole;
import com.ssafy.meshroom.backend.global.auth.error.AuthFailHandlerFilter;
import com.ssafy.meshroom.backend.global.auth.error.SecurityAuthenticationEntryPoint;
import com.ssafy.meshroom.backend.global.auth.error.SecurityDeniedHandler;
import com.ssafy.meshroom.backend.global.auth.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Spring Security 설정을 위한 설정 클래스.
 * 이 클래스는 HTTP 보안 설정을 정의하며, JWT 기반 인증을 포함하여 사용자 정의 인증 메커니즘을 구성합니다.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final SecurityAuthenticationEntryPoint securityAuthenticationEntryPoint;
    private final SecurityDeniedHandler securityDeniedHandler;
    private final AuthFailHandlerFilter authFailHandlerFilter;

    /**
     * Spring Security의 필터 체인을 구성합니다.
     *
     * @param http HttpSecurity를 통해 보안 구성을 정의
     * @return 구성된 SecurityFilterChain
     * @throws Exception 보안 구성 중 예외 발생 시
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/swagger-ui/**").permitAll()
//                        .requestMatchers("/api-docs/**").permitAll()
//                        .requestMatchers("**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/v1/sessions").anonymous()
                        .requestMatchers(HttpMethod.POST,"/api/v1/sessions/**").anonymous()
                        .requestMatchers(HttpMethod.PATCH,"/api/v1/sessions/**/group-name").hasAuthority(UserRole.TEAM_LEADER.getAuthority())
                        .requestMatchers(HttpMethod.PATCH,"/api/v1/sessions/**").hasAuthority(UserRole.FACILITATOR.getAuthority())
//                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())              // 그 외의 모든 요청은 인증 필요)
                .exceptionHandling(e-> {
                            e.accessDeniedHandler(securityDeniedHandler);
                            e.authenticationEntryPoint(securityAuthenticationEntryPoint);
                    }
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(corsFilter(), jwtAuthenticationFilter.getClass())
                .addFilterBefore(authFailHandlerFilter, jwtAuthenticationFilter.getClass());

        // 모든 기본 필터를 비활성화하고 custom filter만 사용하도록 설정
        http
                .exceptionHandling(AbstractHttpConfigurer::disable)
                .headers(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .requestCache(RequestCacheConfigurer::disable)
                .securityContext(AbstractHttpConfigurer::disable)
                .servletApi(AbstractHttpConfigurer::disable)
                .cors(withDefaults()); // CORS 설정 추가

        return http.build();
    }

    /**
     * AuthenticationManager를 제공합니다.
     * 이 메서드는 AuthenticationManager를 구성하고, 사용자 정의 인증 메커니즘을 사용할 수 있도록 합니다.
     *
     * @param authenticationConfiguration 인증 설정
     * @return 구성된 AuthenticationManager
     * @throws Exception 인증 매니저 구성 중 예외 발생 시
     */
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * CORS 필터를 구성합니다.
     *
     * @return 구성된 CorsFilter
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Custom-Header"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
