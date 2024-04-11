package com.studyolle.studyolle.security;

import com.studyolle.studyolle.modules.account.Account;
import com.studyolle.studyolle.modules.account.AccountRepository;
import com.studyolle.studyolle.modules.account.AccountRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class JwtAuthenticationTokenFilter extends GenericFilterBean {

    private static final Pattern BEARER = Pattern.compile("^Bearer$", Pattern.CASE_INSENSITIVE);

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final String headerKey;

    private final Jwt jwt;

    @Autowired
    private AccountRepository accountRepository;

    public JwtAuthenticationTokenFilter ( String header, Jwt jwt ) {
        this.headerKey = header;
        this.jwt = jwt;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            String authorizationToken = obtainAuthorizationToken(request);
            if (authorizationToken != null) {
                try {
                    if(verify(authorizationToken)){
                        String nickname = jwt.getNickname( authorizationToken );
                        Claims claims = Jwts.claims().setSubject(nickname);
                        log.debug("Jwt parse result: {}", claims);

                        Optional<Account> account = accountRepository.findByNickname( nickname );
                        if(account.isPresent()) {
                            JwtAuthenticationToken authentication =
                                    new JwtAuthenticationToken(new JwtAuthentication(account.get().getId(), account.get().getNickname()),
                                            null,
                                            authorities( account.get().getRoles() )
                                            );
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        }
                    }

                } catch (Exception e) {
                    log.warn("Jwt processing failed: {}", e.getMessage());
                }
            }
        } else {
            log.debug("SecurityContextHolder not populated with security token, as it already contained: '{}'",
                    SecurityContextHolder.getContext().getAuthentication());
        }

        chain.doFilter(request, response);
    }

    private String obtainAuthorizationToken(HttpServletRequest request) {
        String token = request.getHeader(headerKey);
        if (token != null) {
            if (log.isDebugEnabled())
                log.debug("Jwt authorization api detected: {}", token);
            try {
                token = URLDecoder.decode(token, "UTF-8");
                String[] parts = token.split(" ");
                if (parts.length == 2) {
                    String scheme = parts[0];
                    String credentials = parts[1];
                    return BEARER.matcher(scheme).matches() ? credentials : null;
                }
            } catch (UnsupportedEncodingException e) {
                log.error(e.getMessage(), e);
            }
        }

        return null;
    }

    private boolean verify(String token) {
        return jwt.validateToken(token);
    }

    private Collection<? extends GrantedAuthority> authorities ( Set<AccountRole> role ) {
        return role.stream().map( r -> new SimpleGrantedAuthority( "ROLE_" + r.name() ) ).collect( Collectors.toSet() );
    }

}