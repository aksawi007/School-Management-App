package org.sma.security.auth.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.sma.security.auth.annotation.RequiredRole;
import org.sma.security.auth.exception.UnauthorizedException;
import org.sma.security.auth.util.SecurityContextUtil;
import org.springframework.stereotype.Component;

/**
 * Aspect for @RequiredRole annotation enforcement
 */
@Aspect
@Component
public class RoleBasedAccessAspect {

    @Before("@annotation(requiredRole)")
    public void checkRole(JoinPoint joinPoint, RequiredRole requiredRole) {
        String[] requiredRoles = requiredRole.value();
        
        if (requiredRoles.length == 0) {
            return;
        }

        boolean hasRole = false;
        for (String role : requiredRoles) {
            if (SecurityContextUtil.hasRole(role)) {
                hasRole = true;
                break;
            }
        }

        if (!hasRole) {
            String username = SecurityContextUtil.getCurrentUsername();
            throw new UnauthorizedException(
                String.format("User '%s' does not have required roles: %s", 
                    username, String.join(", ", requiredRoles))
            );
        }
    }
}
