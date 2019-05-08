package tr.com.manerp.auth

import tr.com.manerp.base.domain.BaseDomain

class ActionItem implements BaseDomain{

    String controllerName
    String actionName
    Boolean shouldBeCleaned

    static hasMany = [actionItemPermissionList:ActionItemPermission]

    static constraints = {

        controllerName nullable: false, maxSize: 254
        actionName nullable: false, maxSize: 254
        actionItemPermissionList nullable: true
        shouldBeCleaned nullable: false

    }

    static mapping = {

        actionItemPermissionList cascade: 'all-delete-orphan'
    }
}
