package tr.com.manerp.auth

import tr.com.manerp.base.domain.BaseDomain

class SecuritySubjectPermission implements BaseDomain {

    Boolean dependsOnMenuItem

    static belongsTo = [securitySubject: SecuritySubject, permissionType: PermissionType]
    static hasMany = []

    static constraints = {

        dependsOnMenuItem nullable: true
        securitySubject nullable: false
        permissionType nullable: false
    }

    static mapping = {

        actionItemPermissionList cascade: 'all-delete-orphan'
    }

}
