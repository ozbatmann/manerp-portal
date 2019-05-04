package tr.com.manerp.auth

import manerp.response.plugin.response.ManeResponse
import manerp.response.plugin.response.StatusCode
import tr.com.manerp.base.controller.BaseController

import javax.xml.bind.ValidationException

class RolePermissionController extends BaseController {


    static namespace = "v1"
    static allowedMethods = [index: "GET", show: "GET", save: "POST", update: "PUT", delete: "DELETE"]

    def rolePermissionService

    def show(){

        ManeResponse maneResponse = new ManeResponse()

        RolePermission rolePermission = rolePermissionService.getRolePermission(params.id)

        try {

            if ( !rolePermission ) throw new Exception()

            maneResponse.data =  [id: rolePermission.id, securitySubjectPermission: [id: rolePermission.securitySubjectPermission.id, name: rolePermission.securitySubjectPermission.securitySubject.name+"-"+rolePermission.securitySubjectPermission.permissionType.name]]
            maneResponse.statusCode = StatusCode.OK

        } catch (Exception ex) {

            if ( !rolePermission ) {
                maneResponse.statusCode = StatusCode.BAD_REQUEST
                maneResponse.message = 'Görüntülenmek istenen rol izni sistemde bulunmamaktadır.'
            }

            if ( maneResponse.statusCode.code <= StatusCode.NO_CONTENT.code ) maneResponse.statusCode = StatusCode.INTERNAL_ERROR
            maneResponse.message = maneResponse.message ?: ex.getMessage()
            ex.printStackTrace()
        }

        render maneResponse
    }

    def save(RolePermission rolePermission)
    {

        ManeResponse maneResponse = new ManeResponse()

        try {
            rolePermission.active = true
            rolePermissionService.save(rolePermission)
            maneResponse.statusCode = StatusCode.CREATED
            maneResponse.data = rolePermission.id
            maneResponse.message = 'Rol izni başarıyla kaydedildi.'

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

    def update(RolePermission rolePermission)
    {

        ManeResponse maneResponse = new ManeResponse()

        try {

            rolePermissionService.save(rolePermission)
            maneResponse.statusCode = StatusCode.NO_CONTENT
            maneResponse.message = 'Rol izni başarıyla güncellendi.'

        } catch (ValidationException ex) {

            maneResponse.statusCode = StatusCode.BAD_REQUEST
            maneResponse.message = parseValidationErrors(ex.errors)
            ex.printStackTrace()

        } catch (Exception ex) {

            if ( !rolePermission ) {
                maneResponse.statusCode = StatusCode.BAD_REQUEST
                maneResponse.message = 'Güncellenmek istenen rol izni sistemde bulunmamaktadır.'
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
        RolePermission rolePermission = RolePermission.get(id)

        try {

            rolePermissionService.delete(rolePermission)
            maneResponse.statusCode = StatusCode.NO_CONTENT
            maneResponse.message = 'Rol izni başarıyla silindi.'

        } catch (Exception ex) {

            if ( !rolePermission ) {
                maneResponse.statusCode = StatusCode.BAD_REQUEST
                maneResponse.message = 'Silinmek istenen rol izni sistemde bulunmamaktadır.'
            }

            if ( maneResponse.statusCode.code <= StatusCode.NO_CONTENT.code ) maneResponse.statusCode = StatusCode.INTERNAL_ERROR
            maneResponse.message = maneResponse.message ?: ex.getMessage()
            ex.printStackTrace()
        }

        render maneResponse
    }

}
