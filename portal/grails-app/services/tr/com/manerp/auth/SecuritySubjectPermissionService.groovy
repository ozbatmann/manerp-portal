package tr.com.manerp.auth

import grails.gorm.transactions.Transactional
import manerp.response.plugin.pagination.ManePaginatedResult
import manerp.response.plugin.pagination.ManePaginationProperties

@Transactional
class SecuritySubjectPermissionService {

    ManePaginatedResult getSecuritySubjectPermissionList(ManePaginationProperties properties)
    {

        def closure = {

            eq('active', true)

            if ( !properties.sortPairList ) {
                order('dateCreated', 'desc')
            }

            eq('active', true)

        }

        return paginate(SecuritySubjectPermission, properties, closure)
    }

    SecuritySubjectPermission getSecuritySubjectPermission(String id)
    {
        SecuritySubjectPermission securitySubjectPermission = SecuritySubjectPermission.get(id)

        return securitySubjectPermission
    }

    def save(SecuritySubjectPermission securitySubjectPermission)
    {
        securitySubjectPermission.save(flush: true, failOnError: true)
    }

    def delete(SecuritySubjectPermission securitySubjectPermission)
    {
        securitySubjectPermission.delete(flush: true, failOnError: true)
    }

    def getSecSubPermListDDS(){

        List<SecuritySubjectPermission> list = SecuritySubjectPermission.createCriteria().list {
           eq("active",true)
        } as List<SecuritySubjectPermission>
        return list
    }

    def getAvailableSecSubPermListDDS(String roleId){

        List<String> rolePermList = RolePermission.createCriteria().list {
            role{
                eq("id",roleId)
            }
            projections{
                securitySubjectPermission{
                    property("id")
                }
            }
        } as List<String>

        List<SecuritySubjectPermission> list = SecuritySubjectPermission.createCriteria().list {
            eq("active",true)

            if(rolePermList.size() > 0) {
                not { 'in'('id', rolePermList) }
            }

        } as List<SecuritySubjectPermission>
        return list
    }
}
