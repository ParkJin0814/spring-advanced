package org.example.expert.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.user.enums.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;

@Slf4j
public class AdminLogInterceptor implements HandlerInterceptor {
    private static final String[] WHITE_LIST = {"/admin*"};
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminLogInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestURI = request.getRequestURI();
        // Admin만 요청할 수 있는 URL 이아니면 로깅을 하지않음
        if (!isWhiteList(requestURI)) {
            return true;
        }

        // 유저 권한 확인
        UserRole userRole = (UserRole) request.getAttribute("userRole");

        // 권한이 없을 경우 에러메세지 전달 이후 진행 x
        if (!UserRole.ADMIN.equals(userRole)) {
            throw new AccessDeniedException("관리자 권한이 없습니다.");
        }

        // 인증성공시 요청시간 URL 로깅
        LOGGER.info("REQUEST 요청시간: [{}], URL: [{}]", LocalDateTime.now(), requestURI);

        return true;
    }

    private boolean isWhiteList(String requestURI) {
        return PatternMatchUtils.simpleMatch(WHITE_LIST, requestURI);
    }
}
