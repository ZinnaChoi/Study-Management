package mogakco.StudyManagement.config;

import java.io.IOException;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mogakco.StudyManagement.domain.Member;
import mogakco.StudyManagement.dto.MemberDetails;
import mogakco.StudyManagement.enums.MemberRole;
import mogakco.StudyManagement.util.JWTUtil;

// JWT 인증 및 인가 관련 필터
@Configuration
public class JWTConfig extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    private final RedisTemplate<String, Object> redisTemplate;

    public JWTConfig(JWTUtil jwtUtil, RedisTemplate<String, Object> redisTemplate) {
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // request에서 Authorization 헤더를 찾음
        String authorization = request.getHeader("Authorization");

        // Authorization 헤더 검증(널이거나 RFC 7235 정의에 맞지 않는 양식이면)
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            // 다음 필터 실행
            filterChain.doFilter(request, response);
            // 조건이 해당되면 메소드 종료
            return;
        }

        // Bearer 부분 제거 후 순수 토큰만 획득
        String token = authorization.split(" ")[1];

        // 토큰 소멸 시간 검증
        if (jwtUtil.isExpired(token)) {
            // 다음 필터 실행
            filterChain.doFilter(request, response);
            // 조건이 해당되면 메소드 종료
            return;
        }

        String key = "JWT:" + jwtUtil.getUserId(token);
        String storedToken = (String) redisTemplate.opsForValue().get(key);

        // 로그아웃을 하지 않은 경우 정상 동작
        if (redisTemplate.hasKey(key) && storedToken != null) {
            // 토큰에서 id와 role 획득
            String id = jwtUtil.getUserId(token);
            MemberRole role = MemberRole.fromString(jwtUtil.getRole(token));

            // member를 생성하여 값 set
            Member member = Member.builder().id(id).role(role).build();

            // UserDetails에 회원 정보 객체 담기
            MemberDetails memberDetails = new MemberDetails(member);

            // 스프링 시큐리티 인증 토큰 생성
            Authentication authToken = new UsernamePasswordAuthenticationToken(memberDetails, null,
                    memberDetails.getAuthorities());

            // 세션에 사용자 등록
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        // 다음 필터 실행
        filterChain.doFilter(request, response);
    }

}
