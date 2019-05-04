package tr.com.manerp.common

import tr.com.manerp.auth.SecuritySubjectPermission
import tr.com.manerp.base.domain.BaseDomain
import tr.com.manerp.enumeration.MenuItemType

class MenuItem implements BaseDomain{

    String name
    String description
    Integer orderNo
    String url
    MenuItemType type
    byte[] icon
    Menu menu
    SecuritySubjectPermission securitySubjectPermission

    static belongsTo = [parent:MenuItem]
    static hasMany = [childs:MenuItem]

    static constraints = {
        name nullable:false
        description nullable:true
        orderNo nullable:false
        type nullable: false
        url nullable:true
        icon nullable:true
        parent  nullable:true
        menu nullable: false
        securitySubjectPermission nullable: true
        childs nullable: true
    }

    static mapping = {
        childs cascade: 'all-delete-orphan'
        menu cascade: 'all-delete-orphan'
    }
}
