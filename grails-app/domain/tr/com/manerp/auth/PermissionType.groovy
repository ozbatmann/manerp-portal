package tr.com.manerp.auth

import tr.com.manerp.base.domain.BaseDomain

class PermissionType implements BaseDomain {

    String name

    static hasMany = [securitySubjectPermissionList:SecuritySubjectPermission]

    static constraints = {

        name nullable:false, maxSize: 254
        securitySubjectPermissionList nullable: true
    }

    static mapping = {

        securitySubjectPermissionList cascade: 'all-delete-orphan'
    }
}

