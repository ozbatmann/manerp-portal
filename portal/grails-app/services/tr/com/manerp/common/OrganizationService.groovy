package tr.com.manerp.common

import grails.gorm.transactions.Transactional
import manerp.response.plugin.pagination.ManePaginatedResult
import manerp.response.plugin.pagination.ManePaginationProperties
import tr.com.manerp.base.service.BaseService
import tr.com.manerp.user.UserOrganizationRole

@Transactional
class OrganizationService extends BaseService{

    ManePaginatedResult getOrganizationList(ManePaginationProperties properties)
    {

        def closure = {

            eq('active', true)

            if ( !properties.sortPairList ) {
                order('dateCreated', 'desc')
            }

            eq('active', true)

        }

        return paginate(Organization, properties, closure)
    }

    Organization getOrganization(String id)
    {
        Organization organization = Organization.get(id)

        return organization
    }

    def save(Organization organization)
    {
        organization.save(flush: true, failOnError: true)
    }

    def delete(Organization organization)
    {
        organization.delete(flush: true, failOnError: true)
    }

    List<Organization> getOrganizationListDDS(){

      List<Organization> organizationList =  Organization.createCriteria().list {
            eq("active",true)
        } as List<Organization>

        return organizationList
    }


}
