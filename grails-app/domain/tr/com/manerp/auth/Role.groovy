package tr.com.manerp.auth

import tr.com.manerp.base.domain.BaseDomain

import java.security.Permission

class Role implements BaseDomain{

    String name
    String description
    String key

    static hasMany = [rolePermission:RolePermission]

    static constraints = {
        name nullable: false
        description nullable: true
        key nullable: true
    }
    static mapping = {
        rolePermission cascade: 'all-delete-orphan'
    }
}

