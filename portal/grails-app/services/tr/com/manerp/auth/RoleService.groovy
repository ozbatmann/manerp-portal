package tr.com.manerp.auth

import grails.gorm.transactions.Transactional
import manerp.response.plugin.pagination.ManePaginatedResult
import manerp.response.plugin.pagination.ManePaginationProperties
import tr.com.manerp.base.service.BaseService

@Transactional
class RoleService extends BaseService{

    ManePaginatedResult getRoleList(ManePaginationProperties properties)
    {

        def closure = {

            eq('active', true)

            if ( !properties.sortPairList ) {
                order('dateCreated', 'desc')
            }

            eq('active', true)

        }

        return paginate(Role, properties, closure)
    }

    Role getRole(String id)
    {
        Role role = Role.get(id)

        return role
    }

    def save(Role role)
    {
        role.save(flush: true, failOnError: true)
    }

    def delete(Role role)
    {
        role.delete(flush: true, failOnError: true)
    }

    List<Role> getRoleListDDS(){

        List<Role> list = Role.createCriteria().list {
            eq("active",true)
        } as List<Role>

        return list
    }

    ManePaginatedResult getRolePermissionList(ManePaginationProperties properties,String roleId){

        def closure = {

            eq('active', true)

            if ( !properties.sortPairList ) {
                order('dateCreated', 'desc')
            }

            eq('active', true)

            role{
                eq("id",roleId)
            }

        }
        return paginate(RolePermission, properties, closure)
    }
}
