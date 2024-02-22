package mogakco.StudyManagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import mogakco.StudyManagement.util.JWTUtil;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JWTUtil jwtUtil;

    private final RedisTemplate<String, Object> redisTemplate;

    public SecurityConfig(JWTUtil jwtUtil, RedisTemplate<String, Object> redisTemplate) {
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // csrf disable -> 세션 인증 방식이 아닌 jwt 방식에서는 필요 없음
        http.csrf((auth) -> auth.disable());
        // form 로그인 방식(default 방식) disable -> jwt 방식이므로
        http.formLogin((auth) -> auth.disable());
        // http basic 인증 방식 disable
        http.httpBasic((auth) -> auth.disable());
        // 경로별 인가 작업
        http.authorizeHttpRequests((auth) -> auth
                .requestMatchers("/", "api/v1/login", "api/v1/join", "api/v1/join/check-id")
                .permitAll() // 회원가입, 로그인, 중복 아이디 확인은 인증 X
                .requestMatchers(HttpMethod.GET, "api/v1/schedules").permitAll()
                .requestMatchers("/swagger-ui.html", "/v1/api-docs/**", "/swagger-ui/**", "/swagger-resources/**")
                .permitAll() // swagger 경로
                // 접근 허용
                // 회원탈퇴는 USER만 가능
                .requestMatchers(HttpMethod.DELETE, "/api/v1/withdraw").hasAuthority("USER")
                // /api/v1/study/** 요청 중 get 메소드만 모든 권한 접근 가능
                .requestMatchers(HttpMethod.GET, "/api/v1/study/**").hasAnyAuthority("ADMIN", "USER")
                .requestMatchers("/api/v1/study/**").hasAuthority("ADMIN") // admin 권한만 접근 가능!
                .requestMatchers("/api/v1/**").hasAnyAuthority("ADMIN", "USER") // 모든 권한 api/user/** 접근 가능!
                .anyRequest().authenticated()); // 그 외 요청들은 인증된 사용자(유효한 JWT를 가지고있는)만 접근 가능
        // 로그인 필터 전에 동작
        http.addFilterBefore(new JWTConfig(jwtUtil, redisTemplate), UsernamePasswordAuthenticationFilter.class);
        // 세션 stateless 설정
        http.sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    // AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
