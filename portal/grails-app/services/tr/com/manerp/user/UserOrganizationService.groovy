package tr.com.manerp.user

import grails.gorm.transactions.Transactional
import tr.com.manerp.auth.Role
import tr.com.manerp.base.service.BaseService

@Transactional
class UserOrganizationService extends BaseService{

    def save(UserOrganization userOrganization)
    {
        userOrganization.save(flush: true, failOnError: true)
    }

    def delete(UserOrganization userOrganization)
    {
        userOrganization.delete(flush: true, failOnError: true)
    }
}