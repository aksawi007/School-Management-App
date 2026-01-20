# SMA Security & Authorization Module

A comprehensive, reusable security module providing role-based access control (RBAC) and token-based authorization for the School Management Application ecosystem.

## Features

### 1. **Token-Based Authorization (JWT)**
   - JWT token generation and validation
   - Refresh token support
   - Configurable expiration times
   - Claims-based authentication

### 2. **Role-Based Access Control (RBAC)**
   - Multiple role support (ADMIN, MANAGER, USER, GUEST, etc.)
   - Role hierarchies
   - Fine-grained permission management
   - Custom role definitions

### 3. **Permission Management**
   - Resource-based permissions
   - Action-based permissions (READ, CREATE, UPDATE, DELETE)
   - Permission assignment to roles
   - Dynamic permission evaluation

### 4. **Annotation-Based Security**
   - `@RequiredRole` - Enforce role-based access on methods
   - `@RequiredPermission` - Enforce permission-based access
   - Aspect-oriented programming for seamless integration

### 5. **Security Utilities**
   - JWT token provider with full lifecycle management
   - Security context utilities for easy access to user info
   - Token validation and expiration checking

## Architecture

```
sma-security-auth/
├── annotation/          # Custom security annotations
├── aspect/              # AOP aspects for security enforcement
├── config/              # Security configuration
├── dto/                 # Data transfer objects
├── exception/           # Custom security exceptions
├── filter/              # JWT authentication filter
├── model/               # Security models (User, Role, Permission)
├── service/             # Service interfaces
├── util/                # Utility classes
└── SecurityConstants.java  # Constants definitions
```

## Usage

### 1. **Add Dependency to Your Service**

```xml
<dependency>
    <groupId>org.sma.app</groupId>
    <artifactId>sma-security-auth</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### 2. **Configure Application Properties**

```properties
# JWT Configuration
app.security.jwt-secret=your-secret-key-here
app.security.jwt-expiration-in-ms=86400000
app.security.jwt-refresh-expiration-in-ms=604800000

# CORS Configuration
app.security.enable-cors=true

# Exclude Patterns (No auth required)
app.security.exclude-patterns=/auth/**,/public/**,/actuator/**
```

### 3. **Register JWT Filter in Your Service**

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .exceptionHandling()
            .and()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
                .antMatchers("/auth/**", "/public/**").permitAll()
                .anyRequest().authenticated()
            .and()
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }
}
```

### 4. **Use Role-Based Access Control**

```java
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("/users")
    @RequiredRole("ADMIN")
    public ResponseEntity<?> getUsers() {
        // Only users with ADMIN role can access this
        return ResponseEntity.ok("Users list");
    }

    @PostMapping("/users")
    @RequiredRole({"ADMIN", "MANAGER"})
    public ResponseEntity<?> createUser(@RequestBody UserDto userDto) {
        // Only ADMIN or MANAGER roles can access
        return ResponseEntity.ok("User created");
    }
}
```

### 5. **Access Current User Information**

```java
public class UserService {
    
    public void performAction() {
        // Get current username
        String currentUser = SecurityContextUtil.getCurrentUsername();
        
        // Check if user has role
        if (SecurityContextUtil.hasRole("ADMIN")) {
            // Perform admin action
        }
        
        // Check if user has any of multiple roles
        if (SecurityContextUtil.hasAnyRole("ADMIN", "MANAGER")) {
            // Perform authorized action
        }
    }
}
```

### 6. **Generate JWT Token**

```java
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        try {
            JwtTokenDto tokenResponse = authService.authenticate(loginRequest);
            return ResponseEntity.ok(tokenResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String token) {
        try {
            JwtTokenDto tokenResponse = authService.refreshToken(token);
            return ResponseEntity.ok(tokenResponse);
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
```

## Model Definitions

### User Model
```java
public class User {
    private Long id;
    private String username;
    private String email;
    private String password;
    private boolean active;
    private Set<Role> roles;
}
```

### Role Model
```java
public class Role {
    private Long id;
    private String name;           // e.g., "ADMIN", "USER", "MANAGER"
    private String description;
    private Set<Permission> permissions;
}
```

### Permission Model
```java
public class Permission {
    private Long id;
    private String name;           // e.g., "USER_CREATE"
    private String description;
    private String resource;       // e.g., "USER"
    private String action;         // READ, CREATE, UPDATE, DELETE
}
```

## Default Roles

- **ADMIN** - Full system access
- **MANAGER** - Management and oversight access
- **USER** - Standard user access
- **GUEST** - Limited read-only access

## Default Permissions

- **READ** - View/read resources
- **CREATE** - Create new resources
- **UPDATE** - Modify existing resources
- **DELETE** - Remove resources

## Exception Handling

The module provides custom exceptions:

- **UnauthorizedException** - Thrown when user lacks required roles
- **InvalidTokenException** - Thrown for invalid or expired tokens

```java
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<?> handleUnauthorized(UnauthorizedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse("Unauthorized", e.getMessage()));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<?> handleInvalidToken(InvalidTokenException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("Invalid Token", e.getMessage()));
    }
}
```

## Integration with Other Modules

Each module using this security library should:

1. Add the dependency to `pom.xml`
2. Configure JWT properties in `application.properties`
3. Implement the `AuthenticationService` and `AuthorizationService` interfaces
4. Register the `JwtAuthenticationFilter` in their security configuration
5. Use annotations on controller methods for role/permission enforcement

## Best Practices

1. **Secret Management** - Never hardcode secrets in code; use environment variables or secure vaults
2. **Token Expiration** - Set reasonable expiration times based on security requirements
3. **HTTPS Only** - Always use HTTPS in production to protect tokens in transit
4. **Token Storage** - Advise clients to store tokens securely (HttpOnly cookies preferred)
5. **Refresh Token Rotation** - Consider rotating refresh tokens on each refresh operation
6. **Audit Logging** - Log authentication and authorization events
7. **Password Hashing** - Use bcrypt or similar for password storage

## Contributing

When extending this module:
- Maintain backward compatibility
- Add comprehensive tests
- Update documentation
- Follow existing code patterns

## License

This module is part of the School Management Application and follows the same license.
