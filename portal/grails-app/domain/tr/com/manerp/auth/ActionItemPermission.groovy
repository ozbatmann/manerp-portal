package tr.com.manerp.auth

import tr.com.manerp.base.domain.BaseDomain

class ActionItemPermission implements BaseDomain{

    static belongsTo = [actionItem: ActionItem, securitySubjectPermission: SecuritySubjectPermission]

    static constraints = {

        actionItem nullable: false
        securitySubjectPermission nullable: false
    }
}
