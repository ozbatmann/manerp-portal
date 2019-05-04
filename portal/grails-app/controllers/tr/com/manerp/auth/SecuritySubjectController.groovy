package tr.com.manerp.auth

import manerp.response.plugin.pagination.ManePaginatedResult
import manerp.response.plugin.pagination.ManePaginationProperties
import manerp.response.plugin.response.ManeResponse
import manerp.response.plugin.response.StatusCode
import tr.com.manerp.base.controller.BaseController
import tr.com.manerp.common.commands.PaginationCommand

import javax.xml.bind.ValidationException

class SecuritySubjectController extends BaseController {

    static namespace = "v1"
    static allowedMethods = [index: "GET", show: "GET", save: "POST", update: "PUT", delete: "DELETE"]

    def securitySubjectService

    def index()
    {

        ManeResponse maneResponse = new ManeResponse()

        try {

            PaginationCommand cmd = new PaginationCommand(params)

            ManePaginatedResult result = securitySubjectService.getSecuritySubjectList(new ManePaginationProperties(cmd.limit, cmd.offset, cmd.sort, cmd.fields))
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

        SecuritySubject securitySubject = securitySubjectService.getSecuritySubject(id)

        try {

            if ( !securitySubject ) throw new Exception()

            maneResponse.data = securitySubject
            maneResponse.statusCode = StatusCode.OK

        } catch (Exception ex) {

            if ( !securitySubject ) {
                maneResponse.statusCode = StatusCode.BAD_REQUEST
                maneResponse.message = 'Görüntülenmek istenen yetki nesnesi sistemde bulunmamaktadır.'
            }

            if ( maneResponse.statusCode.code <= StatusCode.NO_CONTENT.code ) maneResponse.statusCode = StatusCode.INTERNAL_ERROR
            maneResponse.message = maneResponse.message ?: ex.getMessage()
            ex.printStackTrace()
        }

        render maneResponse
    }

    def save(SecuritySubject securitySubject)
    {

        ManeResponse maneResponse = new ManeResponse()

        try {
            securitySubject.active = true
            securitySubjectService.save(securitySubject)
            maneResponse.statusCode = StatusCode.CREATED
            maneResponse.data = securitySubject.id
            maneResponse.message = 'Yetki nesnesi başarıyla kaydedildi.'

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

    def update(SecuritySubject securitySubject)
    {

        ManeResponse maneResponse = new ManeResponse()

        try {

            securitySubjectService.save(securitySubject)
            maneResponse.statusCode = StatusCode.NO_CONTENT
            maneResponse.message = 'Yetki nesnesi başarıyla güncellendi.'

        } catch (ValidationException ex) {

            maneResponse.statusCode = StatusCode.BAD_REQUEST
            maneResponse.message = parseValidationErrors(ex.errors)
            ex.printStackTrace()

        } catch (Exception ex) {

            if ( !securitySubject ) {
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
        SecuritySubject securitySubject = SecuritySubject.get(id)

        try {

            securitySubjectService.delete(securitySubject)
            maneResponse.statusCode = StatusCode.NO_CONTENT
            maneResponse.message = 'Yetki Nesnesi başarıyla silindi.'

        } catch (Exception ex) {

            if ( !securitySubject ) {
                maneResponse.statusCode = StatusCode.BAD_REQUEST
                maneResponse.message = 'Silinmek istenen yetki nesnesi sistemde bulunmamaktadır.'
            }

            if ( maneResponse.statusCode.code <= StatusCode.NO_CONTENT.code ) maneResponse.statusCode = StatusCode.INTERNAL_ERROR
            maneResponse.message = maneResponse.message ?: ex.getMessage()
            ex.printStackTrace()
        }

        render maneResponse
    }

    def securitySubjectPermissionList() {
        ManeResponse maneResponse = new ManeResponse()

        try {

            PaginationCommand cmd = new PaginationCommand(params)

            ManePaginatedResult result = securitySubjectService.getSecuritySubjectPermissionList(new ManePaginationProperties(cmd.limit, cmd.offset, cmd.sort, cmd.fields),params.id)
            result.data = result.data.collect {
                return [id: it.id, dependsOnMenuItem: it.dependsOnMenuItem, securitySubject: [id: it.securitySubject.id, name: it.securitySubject.name],
                        permissionType: [id: it.permissionType.id, name: it.permissionType.name]]
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
