package by.belstu.it.lyskov.dbrestaurant.controller.filter;

import by.belstu.it.lyskov.dbrestaurant.auth.JwtManager;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log4j2
@Component
@WebFilter(filterName = "JwtFilter")
public class JwtFilter extends OncePerRequestFilter {

    private final JwtManager jwtManager;
    private final UserDetailsService userDetailsService;

    public JwtFilter(JwtManager jwtManager, UserDetailsService userDetailsService) {
        this.jwtManager = jwtManager;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        String token = null;
        String login = null;
        if (authorization != null && authorization.startsWith("Bearer ")) {
            token = authorization.substring(7);
            try {
                login = jwtManager.getLoginFromJwt(token);
            } catch (IllegalArgumentException e) {
                log.warn("Unable to get JWT token");
            } catch (ExpiredJwtException e) {
                log.warn("JWT token has expired");
            } catch (MalformedJwtException e) {
                log.error("Invalid JWT structure");
            }
        } else login = "";
        if (login != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(login);
            if (login.length() == 0 || jwtManager.validateJwt(token, userDetails)) {
                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));
            } else {
                log.warn("Invalid JWT token!");
            }
        }
        filterChain.doFilter(request, response);
    }
}
