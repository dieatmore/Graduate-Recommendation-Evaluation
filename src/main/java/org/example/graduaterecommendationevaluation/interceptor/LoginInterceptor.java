package org.example.graduaterecommendationevaluation.interceptor;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.graduaterecommendationevaluation.component.JWTComponent;
import org.example.graduaterecommendationevaluation.exception.Code;
import org.example.graduaterecommendationevaluation.exception.XException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {
    private final JWTComponent jwtComponent;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        if(token == null) {
            throw XException.builder().code(Code.UNAUTHORIZED).build();
        }
        DecodedJWT decode = jwtComponent.decode(token);
        Long uid = decode.getClaim("uid").asLong();
        String role = decode.getClaim("role").asString();
        request.setAttribute("uid",uid);
        request.setAttribute("role",role);
        if(!decode.getClaim("collegeId").isMissing()) {
            request.setAttribute("collegeId", decode.getClaim("collegeId").asLong());
        }
        if (!decode.getClaim("catsId").isMissing()) {
            request.setAttribute("catsId", decode.getClaim("catsId").asList(Long.class));
        }
        if (!decode.getClaim("catId").isMissing()) {
            request.setAttribute("catId", decode.getClaim("catId").asLong());
        }
        if (!decode.getClaim("majorId").isMissing()) {
            request.setAttribute("majorId", decode.getClaim("majorId").asLong());
        }
        return true;
    }
}
