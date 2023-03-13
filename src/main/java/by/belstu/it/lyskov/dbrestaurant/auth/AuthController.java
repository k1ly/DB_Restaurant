package by.belstu.it.lyskov.dbrestaurant.auth;

import by.belstu.it.lyskov.dbrestaurant.controller.cookie.CookieManager;
import by.belstu.it.lyskov.dbrestaurant.controller.cookie.RememberMeDto;
import by.belstu.it.lyskov.dbrestaurant.controller.mapper.DtoMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;

@RestController
public class AuthController {

    @Value("${cookie.expiration}")
    private Integer expiration;
    private final AuthenticationManager authenticationManager;
    private final JwtManager jwtManager;
    private final CookieManager cookieManager;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final DtoMapper dtoMapper;

    public AuthController(AuthenticationManager authenticationManager, JwtManager jwtManager, CookieManager cookieManager,
                          PasswordEncoder passwordEncoder, UserDetailsService userDetailsService, DtoMapper dtoMapper) {
        this.authenticationManager = authenticationManager;
        this.jwtManager = jwtManager;
        this.cookieManager = cookieManager;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
        this.dtoMapper = dtoMapper;
    }

    @PostConstruct
    private void configure() {
        dtoMapper.addTypeMapping(UserDetailsInfo.class, UserAuthDto.class, UserDetailsInfo::getOrder, UserAuthDto::setOrder);
    }

    @PostMapping("/login")
    public String createToken(@RequestBody(required = false) AuthDto authDto, @RequestParam(name = "remember-me", required = false) String remember,
                              @CookieValue(name = "remember-me", required = false) String rememberMe,
                              HttpServletResponse response) throws AuthenticationException {
        UserDetails userDetails;
        if (rememberMe != null) {
            RememberMeDto rememberMeDto = cookieManager.parse(rememberMe, RememberMeDto.class);
            userDetails = userDetailsService.loadUserByUsername(rememberMeDto.getLogin());
            authDto = new AuthDto();
            authDto.setLogin(rememberMeDto.getLogin());
            authDto.setPassword(new String(Base64.getDecoder().decode(rememberMeDto.getHash()))
                    .substring(rememberMeDto.getLogin().length()));
            if (!passwordEncoder.matches(authDto.getPassword(), userDetails.getPassword()))
                throw new InvalidCookieException("Invalid \"remember-me\" cookie");
        } else userDetails = userDetailsService.loadUserByUsername(authDto.getLogin());
        if (remember != null) {
            RememberMeDto rememberMeDto = new RememberMeDto();
            rememberMeDto.setLogin(userDetails.getUsername());
            rememberMeDto.setHash(Base64.getEncoder().encodeToString((authDto.getLogin() + authDto.getPassword()).getBytes()));
            response.addCookie(cookieManager.create("remember-me", rememberMeDto, expiration));
        }
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDto.getLogin(), authDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));
        return jwtManager.generateJwt(userDetails);
    }

    @PostMapping("/login/user")
    public UserAuthDto getUser(@RequestHeader(name = "Authorization", required = false) String authorization,
                               @CookieValue(name = "remember-me", required = false) String rememberMe) throws JwtException, UsernameNotFoundException {
        UserDetails userDetails;
        if (authorization == null && rememberMe != null) {
            RememberMeDto rememberMeDto = cookieManager.parse(rememberMe, RememberMeDto.class);
            userDetails = userDetailsService.loadUserByUsername(rememberMeDto.getLogin());
            if (!passwordEncoder.matches(new String(Base64.getDecoder().decode(rememberMeDto.getHash()))
                    .substring(rememberMeDto.getLogin().length()), userDetails.getPassword()))
                throw new InvalidCookieException("Invalid \"remember-me\" cookie");
        } else {
            String token = null;
            if (authorization != null && authorization.startsWith("Bearer "))
                token = authorization.substring(7);
            try {
                String login = token != null ? jwtManager.getLoginFromJwt(token) : "";
                userDetails = userDetailsService.loadUserByUsername(login);
            } catch (IllegalArgumentException | ExpiredJwtException | MalformedJwtException e) {
                throw new JwtException("Invalid JWT token!");
            }
        }
        return dtoMapper.map(userDetails, UserAuthDto.class);
    }
}
