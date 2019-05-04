package tr.com.manerp.user

import manerp.response.plugin.response.ManeResponse
import manerp.response.plugin.response.StatusCode
import tr.com.manerp.base.controller.BaseController

import javax.xml.bind.ValidationException

class UserOrganizationRoleController extends BaseController {

    static namespace = "v1"
    static allowedMethods = [index: "GET", show: "GET", save: "POST", update: "PUT", delete: "DELETE"]
    def userOrganizationRoleService

    def show(String id)
    {

        ManeResponse maneResponse = new ManeResponse()

        UserOrganizationRole userOrganizationRole = userOrganizationRoleService.getUserOrganizationRole(id)

        try {

            if ( !userOrganizationRole ) throw new Exception()

            maneResponse.data = [id:userOrganizationRole.id, userId: userOrganizationRole.user.id, organization:[id: userOrganizationRole.organization.id, name: userOrganizationRole.organization.name], role:[id: userOrganizationRole.role.id, name: userOrganizationRole.role.name]]
            maneResponse.statusCode = StatusCode.OK

        } catch (Exception ex) {

            if ( !company ) {
                maneResponse.statusCode = StatusCode.BAD_REQUEST
                maneResponse.message = 'Görüntülenmek istenen kullanıcı - organizasyon - rol sistemde bulunmamaktadır.'
            }

            if ( maneResponse.statusCode.code <= StatusCode.NO_CONTENT.code ) maneResponse.statusCode = StatusCode.INTERNAL_ERROR
            maneResponse.message = maneResponse.message ?: ex.getMessage()
            ex.printStackTrace()
        }

        render maneResponse
    }

    def save(UserOrganizationRole userOrganizationRole)
    {

        ManeResponse maneResponse = new ManeResponse()

        try {
            userOrganizationRole.active = true
            userOrganizationRoleService.save(userOrganizationRole)
            maneResponse.statusCode = StatusCode.CREATED
            maneResponse.data = userOrganizationRole.id
            maneResponse.message = 'Kullanıcı - organizasyon - rol başarıyla kaydedildi.'

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

    def update(UserOrganizationRole userOrganizationRole)
    {

        ManeResponse maneResponse = new ManeResponse()

        try {

            userOrganizationRoleService.save(userOrganizationRole)
            maneResponse.statusCode = StatusCode.NO_CONTENT
            maneResponse.message = 'Kullanıcı - organizasyon - rol başarıyla güncellendi.'

        } catch (ValidationException ex) {

            maneResponse.statusCode = StatusCode.BAD_REQUEST
            maneResponse.message = parseValidationErrors(ex.errors)
            ex.printStackTrace()

        } catch (Exception ex) {

            if ( !userOrganizationRole ) {
                maneResponse.statusCode = StatusCode.BAD_REQUEST
                maneResponse.message = 'Güncellenmek istenen Kullanıcı - organizasyon - rol sistemde bulunmamaktadır.'
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
        UserOrganizationRole userOrganizationRole = UserOrganizationRole.get(id)

        try {

            userOrganizationRoleService.delete(userOrganizationRole)
            maneResponse.statusCode = StatusCode.NO_CONTENT
            maneResponse.message = 'Kullanıcı - organizasyon - rol başarıyla silindi.'

        } catch (Exception ex) {

            if ( !userOrganizationRole ) {
                maneResponse.statusCode = StatusCode.BAD_REQUEST
                maneResponse.message = 'Silinmek istenen kullanıcı - organizasyon - rol sistemde bulunmamaktadır.'
            }

            if ( maneResponse.statusCode.code <= StatusCode.NO_CONTENT.code ) maneResponse.statusCode = StatusCode.INTERNAL_ERROR
            maneResponse.message = maneResponse.message ?: ex.getMessage()
            ex.printStackTrace()
        }

        render maneResponse
    }

}
