package com.rua.logic;

import com.rua.exception.ChamberExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component("chamberDelegatedAuthenticationEntryPoint")
public class ChamberDelegatedAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final HandlerExceptionResolver resolver;

    // Lombok @RequiredArgsConstructor do not bring in @Qualifier, create constructor manually
    @Autowired
    public ChamberDelegatedAuthenticationEntryPoint(
            @Qualifier("handlerExceptionResolver") final HandlerExceptionResolver resolver) {
        this.resolver = resolver;
    }

    /**
     * Delegates to {@link HandlerExceptionResolver} to handle {@link AuthenticationException}, in the end the exception
     * is handled in {@link ChamberExceptionHandler#handleAuthenticationException(AuthenticationException)}
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) {
        resolver.resolveException(request, response, null, authException);
    }

}