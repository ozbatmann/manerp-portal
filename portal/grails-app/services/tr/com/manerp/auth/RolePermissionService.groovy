package tr.com.manerp.auth

import grails.gorm.transactions.Transactional
import tr.com.manerp.base.service.BaseService

@Transactional
class RolePermissionService extends BaseService{

    RolePermission getRolePermission(String id){
        RolePermission rolePermission = RolePermission.get(id)

        return rolePermission
    }

    def save(RolePermission rolePermission)
    {
        rolePermission.save(flush: true, failOnError: true)
    }

    def delete(RolePermission rolePermission)
    {
        rolePermission.delete(flush: true, failOnError: true)
    }

}
