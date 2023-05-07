package com.nhnacademy.todolist.interceptor;

import com.nhnacademy.todolist.exception.UnauthorizedUserException;
import com.nhnacademy.todolist.share.UserIdStore;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String userId = request.getHeader("X-USER-ID");
        if(!StringUtils.hasText(userId)){
            throw new UnauthorizedUserException();
        }
        UserIdStore.setUserId(userId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserIdStore.remove();
    }

}
