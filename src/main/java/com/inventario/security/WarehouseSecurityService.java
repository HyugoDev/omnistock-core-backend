package com.inventario.security;

import com.inventario.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WarehouseSecurityService {

    public AuthenticatedUser getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AuthenticatedUser user) {
            return user;
        }
        return null;
    }

    /**
     * Resuelve el almacén accesible según los permisos RBAC del usuario:
     * - Si es owner o admin: tiene acceso a cualquier almacén o a la vista global.
     * - Si es member: queda restringido exclusivamente a su sede asignada.
     */
    public Long resolveAccessibleWarehouseId(Long requestedWarehouseId) {
        AuthenticatedUser user = getCurrentUser();
        if (user == null || user.isGlobalAdmin()) {
            return requestedWarehouseId;
        }

        if (user.getAssignedWarehouseId() != null) {
            if (requestedWarehouseId != null && !requestedWarehouseId.equals(user.getAssignedWarehouseId())) {
                log.warn("Usuario restringido {} intentó acceder al almacén {}", user.getEmail(), requestedWarehouseId);
                throw new BadRequestException("Acceso restringido: Tu usuario está asignado exclusivamente a la bodega ID: " + user.getAssignedWarehouseId());
            }
            return user.getAssignedWarehouseId();
        }

        return requestedWarehouseId;
    }

    /**
     * Valida permisos de modificación para un almacén en transacciones y despachos.
     */
    public void validateCanOperateOnWarehouse(Long warehouseId) {
        AuthenticatedUser user = getCurrentUser();
        if (user == null || user.isGlobalAdmin()) {
            return;
        }

        if (user.getAssignedWarehouseId() != null && !user.getAssignedWarehouseId().equals(warehouseId)) {
            throw new BadRequestException("No tienes permisos para operar en este local/bodega.");
        }
    }
}
