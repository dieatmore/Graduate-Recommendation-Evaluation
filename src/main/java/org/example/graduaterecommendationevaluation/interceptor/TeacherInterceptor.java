package org.example.graduaterecommendationevaluation.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.graduaterecommendationevaluation.dox.User;
import org.example.graduaterecommendationevaluation.exception.Code;
import org.example.graduaterecommendationevaluation.exception.XException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TeacherInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(User.TEACHER.equals(request.getAttribute("role")) || User.COLLAGE_ADMIN.equals(request.getAttribute("role"))) {
            return true;
        }
        throw XException.builder().code(Code.FORBIDDEN).build();
    }
}
