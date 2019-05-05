package tr.com.manerp.auth

import grails.gorm.transactions.Transactional
import tr.com.manerp.base.service.BaseService

@Transactional
class RolePermissionService extends BaseService{

    def redisSyncService


    RolePermission getRolePermission(String id){
        RolePermission rolePermission = RolePermission.get(id)

        return rolePermission
    }

    def save(RolePermission rolePermission)
    {
        List<String> securitySubjectPermissionList = new ArrayList<>()
        rolePermission.save(flush: true)
        securitySubjectPermissionList.add(rolePermission.getSecuritySubjectPermission().getId().toString())

        redisSyncService.updateRolePermFromRedis(rolePermission.getRole(), securitySubjectPermissionList, null)
    }

    def delete(RolePermission rolePermission)
    {
        List<String> securitySubjectPermissionList = new ArrayList<>()
        rolePermission.delete(flush: true)

        securitySubjectPermissionList.add(rolePermission.getSecuritySubjectPermission().getId().toString())

        securitySubjectPermissionList.add(rolePermission.getSecuritySubjectPermission().getId().toString())

        redisSyncService.updateRolePermFromRedis(rolePermission.getRole(), null, securitySubjectPermissionList)
    }

}
