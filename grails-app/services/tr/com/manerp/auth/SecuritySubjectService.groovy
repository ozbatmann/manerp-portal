package tr.com.manerp.auth

import grails.gorm.transactions.Transactional
import manerp.response.plugin.pagination.ManePaginatedResult
import manerp.response.plugin.pagination.ManePaginationProperties
import tr.com.manerp.base.service.BaseService

@Transactional
class SecuritySubjectService extends BaseService {

    ManePaginatedResult getSecuritySubjectList(ManePaginationProperties properties)
    {

        def closure = {

            eq('active', true)

            if ( !properties.sortPairList ) {
                order('dateCreated', 'desc')
            }

            eq('active', true)

        }

        return paginate(SecuritySubject, properties, closure)
    }

    SecuritySubject getSecuritySubject(String id)
    {
        SecuritySubject securitySubject = SecuritySubject.get(id)

        return securitySubject
    }

    def save(SecuritySubject securitySubject)
    {
        securitySubject.save(flush: true, failOnError: true)
    }

    def delete(SecuritySubject securitySubject)
    {
        securitySubject.delete(flush: true, failOnError: true)
    }

    ManePaginatedResult getSecuritySubjectPermissionList(ManePaginationProperties properties,String securitySubjectId){

        def closure = {

            eq('active', true)

            if ( !properties.sortPairList ) {
                order('dateCreated', 'desc')
            }

            eq('active', true)

            securitySubject{
                eq("id",securitySubjectId)
            }

        }
        return paginate(SecuritySubjectPermission, properties, closure)
    }
}
