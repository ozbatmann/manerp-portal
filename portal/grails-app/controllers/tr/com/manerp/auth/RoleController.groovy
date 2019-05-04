package tr.com.manerp.auth

import grails.converters.JSON
import manerp.response.plugin.pagination.ManePaginatedResult
import manerp.response.plugin.pagination.ManePaginationProperties
import manerp.response.plugin.response.ManeResponse
import manerp.response.plugin.response.StatusCode
import tr.com.manerp.base.controller.BaseController
import tr.com.manerp.common.commands.PaginationCommand

import javax.xml.bind.ValidationException

class RoleController extends BaseController {

    static namespace = "v1"
    static allowedMethods = [index: "GET", show: "GET", save: "POST", update: "PUT", delete: "DELETE"]

    def roleService

    def index()
    {

        ManeResponse maneResponse = new ManeResponse()

        try {

            PaginationCommand cmd = new PaginationCommand(params)

            ManePaginatedResult result = roleService.getRoleList(new ManePaginationProperties(cmd.limit, cmd.offset, cmd.sort, cmd.fields))
            maneResponse.data = result.toMap()

        } catch (Exception ex) {

            if ( maneResponse.statusCode.code <= StatusCode.NO_CONTENT.code ) maneResponse.statusCode = StatusCode.INTERNAL_ERROR
            maneResponse.message = ex.getMessage()
            ex.printStackTrace()
        }

        render maneResponse
    }

    def show(String id)
    {

        ManeResponse maneResponse = new ManeResponse()

        Role role = roleService.getRole(id)

        try {

            if ( !role ) throw new Exception()

            maneResponse.data = role
            maneResponse.statusCode = StatusCode.OK

        } catch (Exception ex) {

            if ( !role ) {
                maneResponse.statusCode = StatusCode.BAD_REQUEST
                maneResponse.message = 'Görüntülenmek istenen rol sistemde bulunmamaktadır.'
            }

            if ( maneResponse.statusCode.code <= StatusCode.NO_CONTENT.code ) maneResponse.statusCode = StatusCode.INTERNAL_ERROR
            maneResponse.message = maneResponse.message ?: ex.getMessage()
            ex.printStackTrace()
        }

        render maneResponse
    }

    def save(Role role)
    {

        ManeResponse maneResponse = new ManeResponse()

        try {
            role.active = true
            roleService.save(role)
            maneResponse.statusCode = StatusCode.CREATED
            maneResponse.data = role.id
            maneResponse.message = 'Rol başarıyla kaydedildi.'

        } catch (ValidationException ex) {

            maneResponse.statusCode = StatusCode.BAD_REQUEST
            maneResponse.message = parseValidationErrors(ex.errors)
            ex.printStackTrace()

        } catch (Exception ex) {

            maneResponse.statusCode = StatusCode.INTERNAL_ERROR
            maneResponse.message = ex.getMessage()
            ex.printStackTrace()
        }

        render maneResponse
    }

    def update(Role role)
    {

        ManeResponse maneResponse = new ManeResponse()

        try {

            roleService.save(role)
            maneResponse.statusCode = StatusCode.NO_CONTENT
            maneResponse.message = 'Rol başarıyla güncellendi.'

        } catch (ValidationException ex) {

            maneResponse.statusCode = StatusCode.BAD_REQUEST
            maneResponse.message = parseValidationErrors(ex.errors)
            ex.printStackTrace()

        } catch (Exception ex) {

            if ( !role ) {
                maneResponse.statusCode = StatusCode.BAD_REQUEST
                maneResponse.message = 'Güncellenmek istenen rol sistemde bulunmamaktadır.'
            }

            if ( maneResponse.statusCode.code <= StatusCode.NO_CONTENT.code ) maneResponse.statusCode = StatusCode.INTERNAL_ERROR
            maneResponse.message = maneResponse.message ?: ex.getMessage()
            ex.printStackTrace()
        }

        render maneResponse
    }

    def delete(String id)
    {
        ManeResponse maneResponse = new ManeResponse()
        Role role = Role.get(id)

        try {

            roleService.delete(role)
            maneResponse.statusCode = StatusCode.NO_CONTENT
            maneResponse.message = 'Rol başarıyla silindi.'

        } catch (Exception ex) {

            if ( !role ) {
                maneResponse.statusCode = StatusCode.BAD_REQUEST
                maneResponse.message = 'Silinmek istenen rol sistemde bulunmamaktadır.'
            }

            if ( maneResponse.statusCode.code <= StatusCode.NO_CONTENT.code ) maneResponse.statusCode = StatusCode.INTERNAL_ERROR
            maneResponse.message = maneResponse.message ?: ex.getMessage()
            ex.printStackTrace()
        }

        render maneResponse
    }

    List getRoleListDDS(){

        List<Role> roleList = roleService.getRoleListDDS()

        roleList =  roleList.collect {
            [id: it.id,name: it.name]
        }  as List<Role>

        render roleList as JSON
    }

    def rolePermissionList(){
        ManeResponse maneResponse = new ManeResponse()

        try {

            PaginationCommand cmd = new PaginationCommand(params)

            ManePaginatedResult result = roleService.getRolePermissionList(new ManePaginationProperties(cmd.limit, cmd.offset, cmd.sort, cmd.fields),params.id)
            result.data = result.data.collect {
                return [id: it.id,role: [id: it.role.id, name: it.role.name],securitySubject: [id: it.securitySubjectPermission.securitySubject.id, name: it.securitySubjectPermission.securitySubject.name],
                        permissionType: [id: it.securitySubjectPermission.permissionType.id, name: it.securitySubjectPermission.permissionType.name]]
            }
            maneResponse.data = result.toMap()

        } catch (Exception ex) {

            if (maneResponse.statusCode.code <= StatusCode.NO_CONTENT.code) maneResponse.statusCode = StatusCode.INTERNAL_ERROR
            maneResponse.message = ex.getMessage()
            ex.printStackTrace()
        }

        render maneResponse
    }

}
