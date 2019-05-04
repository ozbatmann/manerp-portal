package tr.com.manerp.user

import grails.gorm.transactions.Transactional
import manerp.response.plugin.pagination.ManePaginatedResult
import manerp.response.plugin.pagination.ManePaginationProperties
import tr.com.manerp.base.service.BaseService

@Transactional
class UserOrganizationRoleService extends BaseService {

    UserOrganizationRole getUserOrganizationRole(String id)
    {
        UserOrganizationRole userOrganizationRole = UserOrganizationRole.get(id)

        return userOrganizationRole
    }


    ManePaginatedResult getOrganizationRoleList(ManePaginationProperties properties, String organizationId){

        def closure = {

            eq('active', true)

            if ( !properties.sortPairList ) {
                order('dateCreated', 'desc')
            }

            eq('active', true)

            organization{
                eq("id",organizationId)
            }

        }
        return paginate(UserOrganizationRole, properties, closure)
    }

    def save(UserOrganizationRole userOrganizationRole) {
        userOrganizationRole.save(flush: true, failOnError: true)
    }
    def delete(UserOrganizationRole userOrganizationRole){
        userOrganizationRole.delete(flush: true, failOnError: true)
    }
}
