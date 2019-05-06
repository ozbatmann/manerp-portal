package tr.com.manerp.user

import grails.databinding.BindingFormat
import tr.com.manerp.base.domain.BaseDomain
import tr.com.manerp.common.Organization

class User implements BaseDomain {

    String username
    byte[] password
    boolean accountExpired = false
    boolean accountLocked = false
    boolean passwordExpired = false
    @BindingFormat('dd/MM/yyyy')
    Date accExpiredDate
    @BindingFormat('dd/MM/yyyy')
    Date accLockedDate
    @BindingFormat('dd/MM/yyyy')
    Date passExpiredDate
    @BindingFormat('dd/MM/yyyy')
    Date lastLogin
    byte[] salt
    Person person

    static hasMany = [userOrganization: UserOrganization, userOrganizationRole: UserOrganizationRole]

    static constraints = {

        username blank: false, unique: true
        person nullable:true,blank:false
        lastLogin nullable: true
        accExpiredDate nullable: true
        accLockedDate nullable: true
        passExpiredDate nullable: true
        password nullable: true
        salt nullable: true
    }

    static mapping = {
        person cascade: 'all-delete-orphan'
        userOrganization cascade: 'all-delete-orphan'
        userOrganizationRole cascade: 'all-delete-orphan'
    }

}