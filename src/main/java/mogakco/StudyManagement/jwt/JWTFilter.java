package mogakco.StudyManagement.jwt;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mogakco.StudyManagement.domain.MemberDetails;
import mogakco.StudyManagement.entity.Member;
import mogakco.StudyManagement.enums.MemberRole;

// JWT 인증 및 인가 관련 필터
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // request에서 Authorization 헤더를 찾음
        String authorization = request.getHeader("Authorization");

        // Authorization 헤더 검증(널이거나 RFC 7235 정의에 맞지 않는 양식이면)
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            System.out.println("token null");
            // 다음 필터 실행
            filterChain.doFilter(request, response);
            // 조건이 해당되면 메소드 종료
            return;
        }

        System.out.println("authorization now");
        // Bearer 부분 제거 후 순수 토큰만 획득
        String token = authorization.split(" ")[1];

        // 토큰 소멸 시간 검증
        if (jwtUtil.isExpired(token)) {
            System.out.println("token expired");
            // 다음 필터 실행
            filterChain.doFilter(request, response);
            // 조건이 해당되면 메소드 종료
            return;
        }

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
        // 다음 필터 실행
        filterChain.doFilter(request, response);
    }

}
