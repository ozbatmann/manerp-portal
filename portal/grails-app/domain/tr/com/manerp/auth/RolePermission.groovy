package tr.com.manerp.auth

import tr.com.manerp.base.domain.BaseDomain

class RolePermission implements BaseDomain{

    Role role
    SecuritySubjectPermission securitySubjectPermission

    static constraints = {
        role nullable: false
        securitySubjectPermission nullable: false
    }
}
