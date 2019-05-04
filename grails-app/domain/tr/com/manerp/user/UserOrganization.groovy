package tr.com.manerp.user

import tr.com.manerp.base.domain.BaseDomain
import tr.com.manerp.common.Organization

class UserOrganization implements BaseDomain {

    Organization organization
    User user

    static constraints = {
        organization nullable: false
        user nullable: false
    }
}
