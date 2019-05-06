package tr.com.manerp.common

import tr.com.manerp.auth.Role
import tr.com.manerp.base.domain.BaseDomain
import tr.com.manerp.user.UserOrganization

class Organization implements BaseDomain {

    String unitCode
    String name
    String description
    byte[] logo
    Organization parent

    static hasMany = [userOrganization: UserOrganization]
    static belongsTo = []

    static constraints = {
        name blank: false, nullable: false, unique: false
        logo nullable: true, blank: true, unique: false
        unitCode blank: true, nullable: true, unique: false, maxSize: 12
        description blank: true, nullable: true, unique: false
        parent nullable: true, blank: true, unique: false
    }

    static mapping = {
        parent cascade: 'all-delete-orphan'
        userOrganization cascade: 'all-delete-orphan'
    }
}