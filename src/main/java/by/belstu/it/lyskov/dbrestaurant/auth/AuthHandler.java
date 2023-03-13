package by.belstu.it.lyskov.dbrestaurant.auth;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log4j2
@Component
public class AuthHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final HandlerExceptionResolver exceptionResolver;

    public AuthHandler(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver) {
        this.exceptionResolver = exceptionResolver;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.error(authException);
        if (request.getHeader(HttpHeaders.ACCEPT).contains(MediaType.TEXT_HTML_VALUE))
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        else
            exceptionResolver.resolveException(request, response, null, authException);
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        if (request.getHeader(HttpHeaders.ACCEPT).contains(MediaType.TEXT_HTML_VALUE))
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        else
            exceptionResolver.resolveException(request, response, null, accessDeniedException);
    }
}
