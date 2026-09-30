package ru.wms.security;

import java.io.IOException;
import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import ru.wms.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // Если заголовок пустой или не начинается с "Bearer ", пропускаем запрос дальше по цепочке
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Извлекаем чистый токен (отсекаем "Bearer " — первые 7 символов)
        jwt = authHeader.substring(7);
        
        try {
            // Извлекаем Email сотрудника из полезной нагрузки токена
            userEmail = jwtService.extractEmail(jwt);

            // Если email успешно извлечен, но пользователь еще не авторизован в текущем потоке (SecurityContext)
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                // Ищем сотрудника в PostgreSQL 16
                var user = userRepository.findByEmail(userEmail).orElse(null);

                // Если токен валиден и соответствует пользователю, формируем объект аутентификации
                if (user != null && jwtService.isTokenValid(jwt, user.getEmail())) {
                    
                    // Превращаем Enum роли (например, ADMIN или WORKER) в понятное для Spring Security право доступа
                    var authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            List.of(authority)
                    );
                    
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // Сохраняем авторизованного сотрудника в глобальный контекст безопасности Spring
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            logger.error("Не удалось обработать JWT токен", e);
        }

        // Пропускаем запрос дальше к контроллерам склада
        filterChain.doFilter(request, response);
    }
}