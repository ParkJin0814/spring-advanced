package org.example.expert.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.expert.domain.adminapilog.entity.AdminApiLog;
import org.example.expert.domain.adminapilog.repository.AdminApiRepository;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminLogInterceptorTest {

    @Mock
    private AdminApiRepository adminApiRepository;

    @InjectMocks
    private AdminLogInterceptor adminLogInterceptor;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    private Object handler;

    @BeforeEach
    void setUp() {
        handler = new Object(); // 더미 핸들러 객체
    }

    @Test
    @DisplayName("관리자 권한 + 화이트리스트 URL → 요청 로그 저장")
    void 관리자_권한일때_요청로그저장됨() throws Exception {
        // given
        given(request.getRequestURI()).willReturn("/admin/dashboard");
        given(request.getAttribute("userRole")).willReturn(UserRole.ADMIN);

        // when
        boolean result = adminLogInterceptor.preHandle(request, response, handler);

        // then
        assertTrue(result);

        ArgumentCaptor<AdminApiLog> captor = ArgumentCaptor.forClass(AdminApiLog.class);
        verify(adminApiRepository, times(1)).save(captor.capture());
        assertEquals("/admin/dashboard", captor.getValue().getRequestUrl());
    }

    @Test
    @DisplayName("일반 사용자 권한으로 관리자 URL 접근 시 → 예외 발생")
    void 일반사용자가_접근시_예외발생() {
        // given
        given(request.getRequestURI()).willReturn("/admin/settings");
        given(request.getAttribute("userRole")).willReturn(UserRole.USER);

        // when & then
        assertThrows(AccessDeniedException.class,
                () -> adminLogInterceptor.preHandle(request, response, handler));

        verify(adminApiRepository, never()).save(any());
    }

    @Test
    @DisplayName("관리자 URL이 아닌 경우 → 아무 작업 없이 통과")
    void 관리자URL아닐때_로깅하지않고통과() throws Exception {
        // given
        given(request.getRequestURI()).willReturn("/user/profile");

        // when
        boolean result = adminLogInterceptor.preHandle(request, response, handler);

        // then
        assertTrue(result);
        verify(adminApiRepository, never()).save(any());
    }
}
