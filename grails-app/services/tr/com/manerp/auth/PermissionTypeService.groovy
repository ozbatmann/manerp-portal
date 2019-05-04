package tr.com.manerp.auth

import grails.gorm.transactions.Transactional
import manerp.response.plugin.pagination.ManePaginatedResult
import manerp.response.plugin.pagination.ManePaginationProperties
import tr.com.manerp.base.service.BaseService

@Transactional
class PermissionTypeService extends BaseService {

    ManePaginatedResult getPermissionTypeList(ManePaginationProperties properties)
    {

        def closure = {

            eq('active', true)

            if ( !properties.sortPairList ) {
                order('dateCreated', 'desc')
            }

            eq('active', true)

        }

        return paginate(PermissionType, properties, closure)
    }

    PermissionType getPermissionType(String id)
    {
        PermissionType permissionType = PermissionType.get(id)

        return permissionType
    }

    def save(PermissionType permissionType)
    {
        permissionType.save(flush: true, failOnError: true)
    }

    def delete(PermissionType permissionType)
    {
        permissionType.delete(flush: true, failOnError: true)
    }
}
