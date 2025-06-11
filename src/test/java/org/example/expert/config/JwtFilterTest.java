package org.example.expert.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    @Mock
    private JwtUtil jwtUtil;
    @InjectMocks
    private JwtFilter jwtFilter;

    @Mock
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    @Mock
    private FilterChain chain;

    @BeforeEach
    void setUp() {
        response = new MockHttpServletResponse();
    }

    @Test
    @DisplayName("URL이 /auth 로 시작하는경우 필터 체인 진행")
    void URL_Auth_로시작하는경우체인진행() throws ServletException, IOException {

        // given
        // url 값 설정 String url = httpRequest.getRequestURI();
        given(request.getRequestURI()).willReturn("/auth/signup");

        // when
        jwtFilter.doFilter(request, response, chain);

        // then
        verify(chain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("JWT 토큰이 없는 경우 - 에러 응답 확인")
    void JWT_토큰_없는경우_에러응답확인() throws ServletException, IOException {

        // given
        // url 값 설정 String url = httpRequest.getRequestURI();
        given(request.getRequestURI()).willReturn("/todos/1/comments");
        // JWT토근 값 널 설정 String bearerJwt = httpRequest.getHeader("Authorization");
        given(request.getHeader("Authorization")).willReturn(null);


        // when
        jwtFilter.doFilter(request, response, chain);

        // then
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
        assertEquals("JWT 토큰이 필요합니다.", response.getErrorMessage());
    }
}