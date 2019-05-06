
package tr.com.manerp.user

import tr.com.manerp.auth.Role
import tr.com.manerp.base.domain.BaseDomain
import tr.com.manerp.common.Organization

class UserOrganizationRole implements BaseDomain {

    User user
    Organization organization
    Role role

    static constraints = {
        user nullable: true
        organization nullable: false
        role nullable: false
    }
}