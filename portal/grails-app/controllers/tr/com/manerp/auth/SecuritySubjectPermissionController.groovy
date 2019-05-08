package tr.com.manerp.auth

import grails.converters.JSON
import manerp.response.plugin.pagination.ManePaginatedResult
import manerp.response.plugin.pagination.ManePaginationProperties
import manerp.response.plugin.response.ManeResponse
import manerp.response.plugin.response.StatusCode
import tr.com.manerp.base.controller.BaseController
import tr.com.manerp.common.commands.PaginationCommand

import javax.xml.bind.ValidationException

class SecuritySubjectPermissionController extends BaseController {

    static namespace = "v1"
    static allowedMethods = [index: "GET", show: "GET", save: "POST", update: "PUT", delete: "DELETE"]

    def securitySubjectPermissionService

    def index()
    {

        ManeResponse maneResponse = new ManeResponse()

        try {

            PaginationCommand cmd = new PaginationCommand(params)

            ManePaginatedResult result = securitySubjectPermissionService.getSecuritySubjectPermissionList(new ManePaginationProperties(cmd.limit, cmd.offset, cmd.sort, cmd.fields))
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

        SecuritySubjectPermission securitySubjectPermission = securitySubjectPermissionService.getSecuritySubjectPermission(id)

        try {

            if ( !securitySubjectPermission ) throw new Exception()

            maneResponse.data = securitySubjectPermission
            maneResponse.statusCode = StatusCode.OK

        } catch (Exception ex) {

            if ( !securitySubjectPermission ) {
                maneResponse.statusCode = StatusCode.BAD_REQUEST
                maneResponse.message = 'Görüntülenmek istenen yetki nesnesi-izin sistemde bulunmamaktadır.'
            }

            if ( maneResponse.statusCode.code <= StatusCode.NO_CONTENT.code ) maneResponse.statusCode = StatusCode.INTERNAL_ERROR
            maneResponse.message = maneResponse.message ?: ex.getMessage()
            ex.printStackTrace()
        }

        render maneResponse
    }

    def save(SecuritySubjectPermission securitySubjectPermission)
    {

        ManeResponse maneResponse = new ManeResponse()

        try {
            securitySubjectPermission.active = true
            securitySubjectPermissionService.save(securitySubjectPermission)
            maneResponse.statusCode = StatusCode.CREATED
            maneResponse.data = securitySubjectPermission.id
            maneResponse.message = 'Yetki nesnesi-İzin başarıyla kaydedildi.'

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

    def update(SecuritySubjectPermission securitySubjectPermission)
    {

        ManeResponse maneResponse = new ManeResponse()

        try {

            securitySubjectPermissionService.save(securitySubjectPermission)
            maneResponse.statusCode = StatusCode.NO_CONTENT
            maneResponse.message = 'Yeetki nesnesi başarıyla güncellendi.'

        } catch (ValidationException ex) {

            maneResponse.statusCode = StatusCode.BAD_REQUEST
            maneResponse.message = parseValidationErrors(ex.errors)
            ex.printStackTrace()

        } catch (Exception ex) {

            if ( !securitySubjectPermission ) {
                maneResponse.statusCode = StatusCode.BAD_REQUEST
                maneResponse.message = 'Güncellenmek istenen yetki nesnesi sistemde bulunmamaktadır.'
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
        SecuritySubjectPermission securitySubjectPermission = SecuritySubjectPermission.get(id)

        try {

            securitySubjectPermissionService.delete(securitySubjectPermission)
            maneResponse.statusCode = StatusCode.NO_CONTENT
            maneResponse.message = 'Yetki nesnesi - İzin başarıyla silindi.'

        } catch (Exception ex) {

            if ( !securitySubjectPermission ) {
                maneResponse.statusCode = StatusCode.BAD_REQUEST
                maneResponse.message = 'Silinmek istenen yetki nesnesi - izin sistemde bulunmamaktadır.'
            }

            if ( maneResponse.statusCode.code <= StatusCode.NO_CONTENT.code ) maneResponse.statusCode = StatusCode.INTERNAL_ERROR
            maneResponse.message = maneResponse.message ?: ex.getMessage()
            ex.printStackTrace()
        }

        render maneResponse
    }

    def getSecSubPermListNotExistInActionsDDS(){

        List<SecuritySubjectPermission> result = securitySubjectPermissionService.getSecSubPermListNotExistInActionsDDS()
        result = result.collect {
            return [id: it.id, dependsOnMenuItem: it.dependsOnMenuItem, securitySubject: [id: it.securitySubject.id, name: it.securitySubject.name+"-"+it.permissionType.name],
                    permissionType: [id: it.permissionType.id, name: it.permissionType.name]]
        } as List<SecuritySubjectPermission>
        render result as JSON
    }

    def getAvailableSecSubPermListDDS(){

        List<SecuritySubjectPermission> result = securitySubjectPermissionService.getAvailableSecSubPermListDDS(params.id)
        result = result.collect {
            return [id: it.id,name: it.securitySubject.name+"-"+it.permissionType.name]
        } as List<SecuritySubjectPermission>
        render result as JSON
    }
}
