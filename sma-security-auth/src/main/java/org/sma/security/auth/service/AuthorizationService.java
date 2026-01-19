package org.sma.security.auth.service;

import org.sma.security.auth.model.Role;
import org.sma.security.auth.model.Permission;
import java.util.List;
import java.util.Set;

/**
 * Interface for Role and Permission management service
 */
public interface AuthorizationService {

    /**
     * Get all roles
     */
    List<Role> getAllRoles();

    /**
     * Get role by name
     */
    Role getRoleByName(String name);

    /**
     * Create new role
     */
    Role createRole(Role role);

    /**
     * Delete role
     */
    void deleteRole(Long roleId);

    /**
     * Get all permissions
     */
    List<Permission> getAllPermissions();

    /**
     * Get permission by name
     */
    Permission getPermissionByName(String name);

    /**
     * Create new permission
     */
    Permission createPermission(Permission permission);

    /**
     * Add permission to role
     */
    void addPermissionToRole(Long roleId, Long permissionId);

    /**
     * Remove permission from role
     */
    void removePermissionFromRole(Long roleId, Long permissionId);

    /**
     * Get permissions by role
     */
    Set<Permission> getPermissionsByRole(String roleName);
}
