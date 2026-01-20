package org.sma.security.auth.model;

import java.io.Serializable;

/**
 * Claims for JWT token payload
 */
public class JwtClaims implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String sub;  // Subject (username)
    private long iat;    // Issued at
    private long exp;    // Expiration time
    private String[] roles;
    private String[] permissions;

    public JwtClaims() {
    }

    public JwtClaims(String sub, long iat, long exp) {
        this.sub = sub;
        this.iat = iat;
        this.exp = exp;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public long getIat() {
        return iat;
    }

    public void setIat(long iat) {
        this.iat = iat;
    }

    public long getExp() {
        return exp;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    public String[] getPermissions() {
        return permissions;
    }

    public void setPermissions(String[] permissions) {
        this.permissions = permissions;
    }
}
